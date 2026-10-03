import requests
from app.config import settings
from app.models.schemas import ProductFeatureItem


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
