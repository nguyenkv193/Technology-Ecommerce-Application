from app.models.schemas import ProductRecommendationItem, UserInteractionItem
from app.services.content_based_recommender import ContentBasedRecommender
from app.services.item_based_collaborative_recommender import ItemBasedCollaborativeRecommender


class HybridRecommender:
    """Blend content similarity with item-item collaborative evidence when available."""

    def __init__(
        self,
        content_model: ContentBasedRecommender,
        collaborative_model: ItemBasedCollaborativeRecommender,
    ) -> None:
        self.content_model = content_model
        self.collaborative_model = collaborative_model

    def recommend_for_user(
        self,
        interactions: list[UserInteractionItem],
        limit: int = 10,
        strategy: str = "hybrid",
    ) -> tuple[str, list[ProductRecommendationItem]]:
        if strategy == "content_based":
            return "content_based", self.content_model.recommend_for_user(interactions, limit)
        if strategy == "collaborative":
            return "collaborative", self.collaborative_model.recommend_for_user(interactions, limit)

        content_results = self.content_model.recommend_for_user(interactions, limit)
        collaborative_results = self.collaborative_model.recommend_for_user(interactions, limit)
        if not collaborative_results:
            return "content_based", content_results
        if not content_results:
            return "collaborative", collaborative_results

        supported_history = len({
            interaction.product_id for interaction in interactions
            if interaction.product_id in self.collaborative_model.product_id_to_idx
        })
        collaborative_weight = min(0.7, 0.15 + 0.1 * max(0, supported_history - 1))
        content_weight = 1.0 - collaborative_weight
        combined: dict[int, float] = {}
        for item in content_results:
            combined[item.product_id] = combined.get(item.product_id, 0.0) + content_weight * item.score
        for item in collaborative_results:
            combined[item.product_id] = combined.get(item.product_id, 0.0) + collaborative_weight * item.score

        by_id = {
            item.product_id: item
            for item in [*content_results, *collaborative_results]
        }
        ranked = sorted(combined, key=lambda product_id: (-combined[product_id], product_id))
        results = [
            ProductRecommendationItem(
                product_id=product_id,
                score=round(combined[product_id], 4),
                reason="Kết hợp mức độ phù hợp nội dung và hành vi của người dùng tương tự",
                source="hybrid",
            )
            for product_id in ranked[:limit]
            if product_id in by_id
        ]
        return "hybrid", results
