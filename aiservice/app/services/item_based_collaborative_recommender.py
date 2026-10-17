import numpy as np
from scipy.sparse import csr_matrix
from sklearn.preprocessing import normalize

from app.models.schemas import (
    ProductFeatureItem,
    ProductRecommendationItem,
    TrainingInteractionItem,
    UserInteractionItem,
)


class ItemBasedCollaborativeRecommender:
    """Item-item cosine model trained only from TechStore's aggregated user-item matrix."""

    MIN_COMMON_USERS = 2

    def __init__(self) -> None:
        self.products: list[ProductFeatureItem] = []
        self.product_id_to_idx: dict[int, int] = {}
        self.similarity: csr_matrix = csr_matrix((0, 0), dtype=np.float32)
        self.training_interaction_count = 0
        self.training_user_count = 0

    def fit(
        self,
        products: list[ProductFeatureItem],
        interactions: list[TrainingInteractionItem],
    ) -> None:
        self.products = list(products)
        self.product_id_to_idx = {product.id: idx for idx, product in enumerate(self.products)}
        self.similarity = csr_matrix((len(products), len(products)), dtype=np.float32)
        self.training_interaction_count = 0
        self.training_user_count = 0

        rows: list[int] = []
        columns: list[int] = []
        values: list[float] = []
        usable_interactions = [
            (item, self.product_id_to_idx[item.product_id])
            for item in interactions
            if item.product_id in self.product_id_to_idx
        ]
        user_ids = sorted({item.user_id for item, _ in usable_interactions})
        user_to_idx = {user_id: idx for idx, user_id in enumerate(user_ids)}

        for item, product_idx in usable_interactions:
            rows.append(user_to_idx[item.user_id])
            columns.append(product_idx)
            values.append(item.score)

        if not values or not products:
            return

        user_item = csr_matrix(
            (np.asarray(values, dtype=np.float32), (rows, columns)),
            shape=(len(user_ids), len(products)),
            dtype=np.float32,
        )
        item_user = user_item.T.tocsr()
        normalized_item_user = normalize(item_user, norm="l2", axis=1, copy=True)
        similarity = (normalized_item_user @ normalized_item_user.T).tocsr()

        binary_item_user = item_user.copy()
        binary_item_user.data = np.ones_like(binary_item_user.data, dtype=np.float32)
        common_users = (binary_item_user @ binary_item_user.T).tocsr()
        common_users.data = (common_users.data >= self.MIN_COMMON_USERS).astype(np.float32)
        common_users.eliminate_zeros()
        similarity = similarity.multiply(common_users).tocsr()
        similarity.setdiag(0)
        similarity.eliminate_zeros()

        self.similarity = similarity
        self.training_interaction_count = len(values)
        self.training_user_count = len(user_ids)

    def recommend_for_user(
        self,
        interactions: list[UserInteractionItem],
        limit: int = 10,
    ) -> list[ProductRecommendationItem]:
        if self.similarity.shape[0] == 0 or not interactions:
            return []

        history: dict[int, float] = {}
        for interaction in interactions:
            product_idx = self.product_id_to_idx.get(interaction.product_id)
            if product_idx is not None:
                history[product_idx] = history.get(product_idx, 0.0) + interaction.score
        if not history:
            return []

        indices = np.fromiter(history.keys(), dtype=np.int32)
        weights = np.fromiter(history.values(), dtype=np.float32)
        profile = csr_matrix(
            (weights, (np.zeros(len(indices), dtype=np.int32), indices)),
            shape=(1, len(self.products)),
            dtype=np.float32,
        )
        scores = np.asarray((profile @ self.similarity).toarray()).ravel()
        max_score = float(scores.max(initial=0.0))
        if max_score <= 0:
            return []
        scores /= max_score

        excluded = {item.product_id for item in interactions}
        ranked = sorted(range(len(scores)), key=lambda idx: (-float(scores[idx]), self.products[idx].id))
        results: list[ProductRecommendationItem] = []
        for idx in ranked:
            score = float(scores[idx])
            product = self.products[idx]
            if product.id in excluded or score <= 0:
                continue
            results.append(ProductRecommendationItem(
                product_id=product.id,
                score=round(score, 4),
                reason="Người dùng có hành vi tương tự cũng quan tâm đến sản phẩm này",
                source="collaborative",
            ))
            if len(results) >= limit:
                break
        return results
