import re
import numpy as np
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
from app.models.schemas import ProductFeatureItem, ProductRecommendationItem, UserInteractionItem


class ContentBasedRecommender:
    """A single TF-IDF model for product similarity and weighted user preferences."""

    def __init__(self):
        self.vectorizer = TfidfVectorizer(token_pattern=r"(?u)\b\w+\b")
        self.tfidf_matrix = None
        self.products: list[ProductFeatureItem] = []
        self.product_id_to_idx: dict[int, int] = {}

    def _extract_content_text(self, product: ProductFeatureItem) -> str:
        description = re.sub(r"<[^>]+>", " ", product.description)
        tokens = [product.name, product.brand, product.category, description]
        for attribute in product.attributes:
            name, value = attribute.get("name", ""), attribute.get("value", "")
            tokens.append(f"{name} {value} {name}_{value}")
        return " ".join(tokens)

    def fit(self, products: list[ProductFeatureItem]):
        self.products = list(products)
        self.product_id_to_idx = {p.id: idx for idx, p in enumerate(products)}
        self.tfidf_matrix = None
        if products:
            self.tfidf_matrix = self.vectorizer.fit_transform(
                [self._extract_content_text(p) for p in products]
            )

    def _rank(self, scores, excluded: set[int], limit: int, category=None):
        candidates = sorted(enumerate(scores), key=lambda x: (-float(x[1]), self.products[x[0]].id))
        results = []
        for idx, score in candidates:
            product = self.products[idx]
            if product.id in excluded or score <= 0.001:
                continue
            if category is not None and self._category(product) != category:
                continue
            results.append(ProductRecommendationItem(
                product_id=product.id,
                score=round(float(score), 4),
                reason="Phù hợp với danh mục và thông số kỹ thuật bạn quan tâm",
            ))
            if len(results) >= limit:
                break
        return results

    @staticmethod
    def _category(product: ProductFeatureItem):
        return product.category_id if product.category_id is not None else product.category

    def recommend_similar(self, product_id: int, limit: int = 10):
        if self.tfidf_matrix is None or product_id not in self.product_id_to_idx:
            return []
        idx = self.product_id_to_idx[product_id]
        scores = cosine_similarity(self.tfidf_matrix[idx], self.tfidf_matrix)[0]
        return self._rank(scores, {product_id}, limit, category=self._category(self.products[idx]))

    def recommend_for_user(self, interactions: list[UserInteractionItem], limit: int = 10):
        if self.tfidf_matrix is None or not interactions:
            return []
        vector = np.zeros((1, self.tfidf_matrix.shape[1]))
        total_weight = 0.0
        excluded = {item.product_id for item in interactions}
        for item in interactions:
            idx = self.product_id_to_idx.get(item.product_id)
            if idx is not None:
                vector += self.tfidf_matrix[idx].toarray() * item.score
                total_weight += item.score
        if total_weight <= 0:
            return []
        vector /= total_weight
        scores = cosine_similarity(vector, self.tfidf_matrix)[0]
        return self._rank(scores, excluded, limit)
