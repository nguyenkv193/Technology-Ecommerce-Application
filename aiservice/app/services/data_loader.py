import requests
from datetime import datetime, timezone
from app.config import settings
from app.models.schemas import ProductFeatureItem, TrainingInteractionItem


class DataLoader:
    """Load the complete real catalog. Empty data is valid; errors never become mock data."""

    @classmethod
    def load_products(cls) -> list[ProductFeatureItem]:
        products = []
        page = 0
        seen = set()
        while True:
            response = requests.get(
                f"{settings.backend_url.rstrip('/')}/products/recommendation-features",
                params={"page": page, "size": 100},
                timeout=settings.request_timeout_seconds,
            )
            response.raise_for_status()
            envelope = response.json()
            if envelope.get("success") is not True:
                raise ValueError("Backend catalog request failed")
            data = envelope["data"]
            for item in data["content"]:
                product = ProductFeatureItem(
                    **{**item, "description": item.get("description") or "",
                       "attributes": [{"name": a["name"], "value": a["value"]}
                                      for a in item.get("attributes", [])]}
                )
                if product.id in seen:
                    raise ValueError("Duplicate product in paginated catalog")
                seen.add(product.id)
                products.append(product)
            if data["last"] is True:
                return products
            if not data["content"]:
                raise ValueError("Invalid empty catalog page")
            page += 1

    @classmethod
    def load_interactions(cls, as_of: datetime | None = None) -> list[TrainingInteractionItem]:
        """Load a bounded, deterministic snapshot of aggregated TechStore interactions."""
        if not settings.backend_api_key:
            return []

        interactions: list[TrainingInteractionItem] = []
        seen: set[tuple[int, int]] = set()
        page = 0
        page_size = 2000
        snapshot_time = as_of or datetime.now(timezone.utc)
        headers = {"X-AI-Service-Key": settings.backend_api_key}

        while len(interactions) < settings.max_training_interactions:
            response = requests.get(
                f"{settings.backend_url.rstrip('/')}/behaviors/recommendation-training-data",
                params={"page": page, "size": page_size, "asOf": snapshot_time.isoformat()},
                headers=headers,
                timeout=settings.request_timeout_seconds,
            )
            response.raise_for_status()
            envelope = response.json()
            if envelope.get("success") is not True:
                raise ValueError("Backend interaction export failed")

            data = envelope["data"]
            for raw_item in data["interactions"]:
                interaction = TrainingInteractionItem.model_validate(raw_item)
                key = (interaction.user_id, interaction.product_id)
                if key in seen:
                    raise ValueError("Duplicate user-product interaction in snapshot")
                seen.add(key)
                interactions.append(interaction)
                if len(interactions) >= settings.max_training_interactions:
                    break

            if data["last"] is True or not data["interactions"]:
                return interactions
            page += 1

        return interactions
