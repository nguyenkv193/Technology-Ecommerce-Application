import math
from collections import defaultdict

from app.models.schemas import (
    ProductFeatureItem,
    TrainingInteractionItem,
    UserInteractionItem,
)
from app.services.content_based_recommender import ContentBasedRecommender
from app.services.hybrid_recommender import HybridRecommender
from app.services.item_based_collaborative_recommender import ItemBasedCollaborativeRecommender


def _metrics(recommended_ids: list[int], relevant_ids: set[int], k: int) -> tuple[float, float, float]:
    top_k = recommended_ids[:k]
    hits = [1 if product_id in relevant_ids else 0 for product_id in top_k]
    hit_count = sum(hits)
    precision = hit_count / k
    recall = hit_count / len(relevant_ids) if relevant_ids else 0.0
    dcg = sum(hit / math.log2(rank + 2) for rank, hit in enumerate(hits))
    ideal_hits = min(k, len(relevant_ids))
    idcg = sum(1.0 / math.log2(rank + 2) for rank in range(ideal_hits))
    ndcg = dcg / idcg if idcg else 0.0
    return precision, recall, ndcg


def evaluate_recommenders(
    products: list[ProductFeatureItem],
    training_interactions: list[TrainingInteractionItem],
    test_interactions: list[TrainingInteractionItem],
    k: int = 10,
) -> dict[str, object]:
    """Evaluate against future interactions while hiding held-out user-item pairs from training."""
    if not 1 <= k <= 50:
        raise ValueError("k must be between 1 and 50")
    product_ids = {product.id for product in products}
    test_pairs = {
        (item.user_id, item.product_id)
        for item in test_interactions
        if item.product_id in product_ids
    }
    training = [
        item for item in training_interactions
        if item.product_id in product_ids and (item.user_id, item.product_id) not in test_pairs
    ]
    training_by_user: dict[int, list[TrainingInteractionItem]] = defaultdict(list)
    test_by_user: dict[int, list[TrainingInteractionItem]] = defaultdict(list)
    for item in training:
        training_by_user[item.user_id].append(item)
    for item in test_interactions:
        if item.product_id in product_ids:
            test_by_user[item.user_id].append(item)

    evaluation_users = [
        (user_id, train_items, test_by_user[user_id])
        for user_id, train_items in training_by_user.items()
        if test_by_user.get(user_id)
    ]

    if not evaluation_users:
        raise ValueError("Not enough users with past and future product interactions for temporal evaluation")

    content_model = ContentBasedRecommender()
    content_model.fit(products)
    collaborative_model = ItemBasedCollaborativeRecommender()
    collaborative_model.fit(products, training)
    hybrid_model = HybridRecommender(content_model, collaborative_model)

    popularity: dict[int, float] = defaultdict(float)
    for interaction in training:
        popularity[interaction.product_id] += interaction.score
    popular_ids = sorted(popularity, key=lambda product_id: (-popularity[product_id], product_id))

    sums: dict[str, list[float]] = {
        "popularity": [0.0, 0.0, 0.0],
        "content_based": [0.0, 0.0, 0.0],
        "collaborative": [0.0, 0.0, 0.0],
        "hybrid": [0.0, 0.0, 0.0],
    }
    for _, train_items, test_items in evaluation_users:
        history = [UserInteractionItem(product_id=item.product_id, score=item.score) for item in train_items]
        seen = {item.product_id for item in train_items}
        relevant = {item.product_id for item in test_items}
        candidate_popular = [product_id for product_id in popular_ids if product_id not in seen][:k]

        recommendations = {
            "popularity": candidate_popular,
            "content_based": [item.product_id for item in content_model.recommend_for_user(history, k)],
            "collaborative": [item.product_id for item in collaborative_model.recommend_for_user(history, k)],
            "hybrid": [item.product_id for item in hybrid_model.recommend_for_user(history, k, "hybrid")[1]],
        }
        for method, product_list in recommendations.items():
            values = _metrics(product_list, relevant, k)
            sums[method] = [current + value for current, value in zip(sums[method], values)]

    users_evaluated = len(evaluation_users)
    metrics = {
        method: {
            f"precision@{k}": round(values[0] / users_evaluated, 6),
            f"recall@{k}": round(values[1] / users_evaluated, 6),
            f"ndcg@{k}": round(values[2] / users_evaluated, 6),
        }
        for method, values in sums.items()
    }
    return {
        "split": "global temporal cutoff; train events at/before cutoff, test user-item pairs after cutoff",
        "k": k,
        "users_evaluated": users_evaluated,
        "training_interactions": len(training),
        "test_interactions": sum(len(test_items) for _, _, test_items in evaluation_users),
        "collaborative_item_pairs": collaborative_model.similarity.nnz,
        "metrics": metrics,
    }
