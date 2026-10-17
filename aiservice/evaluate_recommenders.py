import argparse
import json
import logging
from datetime import timezone

from app.config import settings
from app.services.data_loader import DataLoader
from app.services.offline_evaluation import evaluate_recommenders


def main() -> None:
    parser = argparse.ArgumentParser(description="Temporal offline evaluation for TechStore recommenders")
    parser.add_argument("--k", type=int, default=10, choices=range(1, 51))
    args = parser.parse_args()
    if not settings.backend_api_key:
        raise SystemExit("Set AI_INTERNAL_API_KEY before loading private interaction data")

    products = DataLoader.load_products()
    all_interactions = DataLoader.load_interactions()
    timestamps = sorted(item.last_interacted_at for item in all_interactions)
    if len(timestamps) < 2:
        raise SystemExit("Not enough user-item interaction history for a temporal split")
    cutoff_index = max(0, min(len(timestamps) - 2, int(len(timestamps) * 0.8) - 1))
    cutoff = timestamps[cutoff_index]
    if cutoff.tzinfo is None:
        cutoff = cutoff.replace(tzinfo=timezone.utc)
    train_interactions = DataLoader.load_interactions(as_of=cutoff)
    test_interactions = [item for item in all_interactions if item.last_interacted_at > cutoff]
    report = evaluate_recommenders(products, train_interactions, test_interactions, args.k)
    report["cutoff"] = cutoff.isoformat()
    logging.basicConfig(level=logging.INFO, format="%(message)s")
    logging.getLogger("evaluation").info(json.dumps(report, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()
