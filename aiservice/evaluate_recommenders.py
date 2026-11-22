import argparse
import json
import logging
from datetime import datetime, timezone
from pathlib import Path

from app.config import settings
from app.models.schemas import ProductFeatureItem, TrainingInteractionItem
from app.services.data_loader import DataLoader
from app.services.offline_evaluation import evaluate_recommenders


def parse_iso_datetime(value: str | datetime) -> datetime:
    if isinstance(value, datetime):
        dt = value
    else:
        dt = datetime.fromisoformat(value)
    if dt.tzinfo is None:
        dt = dt.replace(tzinfo=timezone.utc)
    return dt


def format_table(metrics: dict[str, dict[str, float]], k: int) -> str:
    name_map = {
        "popularity": "1. Popularity (Baseline)",
        "content_based": "2. Content-Based (TF-IDF)",
        "collaborative": "3. Item-Item Collaborative Filtering",
        "hybrid": "4. Hybrid Recommender (Content + CF)",
    }

    lines = [
        f"\n{'='*78}",
        f"  BẢNG SO SÁNH HIỆU NĂNG 4 MÔ HÌNH GỢI Ý SẢN PHẨM (OFFLINE EVALUATION, K={k})",
        f"{'='*78}",
        f"| {'Mô hình / Thuật toán':<38} | {f'Precision@{k}':<12} | {f'Recall@{k}':<12} | {f'NDCG@{k}':<12} |",
        f"|:{'-'*38}-|-{'-'*12}:|-{'-'*12}:|-{'-'*12}:|",
    ]

    for key in ["popularity", "content_based", "collaborative", "hybrid"]:
        if key in metrics:
            m = metrics[key]
            p = f"{m.get(f'precision@{k}', 0.0):.4f}"
            r = f"{m.get(f'recall@{k}', 0.0):.4f}"
            n = f"{m.get(f'ndcg@{k}', 0.0):.4f}"
            lines.append(f"| {name_map.get(key, key):<38} | {p:>12} | {r:>12} | {n:>12} |")

    lines.append(f"{'='*78}\n")
    return "\n".join(lines)


def main() -> None:
    parser = argparse.ArgumentParser(description="Temporal offline evaluation for TechStore recommenders (5 models)")
    parser.add_argument("--k", type=int, default=10, choices=range(1, 51), help="Top-K cutoff for evaluation")
    parser.add_argument(
        "--dataset",
        type=str,
        default=None,
        help="Path to offline benchmark dataset JSON (defaults to backend if omitted)",
    )
    parser.add_argument(
        "--output",
        type=str,
        default=None,
        help="Optional path to save evaluation results as JSON",
    )
    args = parser.parse_args()

    if args.dataset:
        dataset_path = Path(args.dataset)
        if not dataset_path.exists():
            raise SystemExit(f"Dataset file not found: {dataset_path}")
        with open(dataset_path, "r", encoding="utf-8") as f:
            raw = json.load(f)
        products = [ProductFeatureItem.model_validate(p) for p in raw["products"]]
        all_interactions = [
            TrainingInteractionItem(
                userId=i["userId"],
                productId=i["productId"],
                score=i["score"],
                lastInteractedAt=parse_iso_datetime(i["lastInteractedAt"]),
            )
            for i in raw["interactions"]
        ]
    else:
        if not settings.backend_api_key:
            raise SystemExit("Set AI_INTERNAL_API_KEY before loading private interaction data from Backend")
        products = DataLoader.load_products()
        all_interactions = DataLoader.load_interactions()

    timestamps = sorted(item.last_interacted_at for item in all_interactions)
    if len(timestamps) < 2:
        raise SystemExit("Not enough user-item interaction history for a temporal split")

    cutoff_index = max(0, min(len(timestamps) - 2, int(len(timestamps) * 0.8) - 1))
    cutoff = timestamps[cutoff_index]
    if cutoff.tzinfo is None:
        cutoff = cutoff.replace(tzinfo=timezone.utc)

    if args.dataset:
        train_interactions = [item for item in all_interactions if item.last_interacted_at <= cutoff]
    else:
        train_interactions = DataLoader.load_interactions(as_of=cutoff)

    test_interactions = [item for item in all_interactions if item.last_interacted_at > cutoff]

    report = evaluate_recommenders(products, train_interactions, test_interactions, args.k)
    report["cutoff"] = cutoff.isoformat()

    # Print pretty evaluation table
    print(format_table(report["metrics"], args.k))

    if args.output:
        out_path = Path(args.output)
        out_path.parent.mkdir(parents=True, exist_ok=True)
        with open(out_path, "w", encoding="utf-8") as f:
            json.dump(report, f, ensure_ascii=False, indent=2)
        print(f"Report saved to {out_path}")
    else:
        logging.basicConfig(level=logging.INFO, format="%(message)s")
        logging.getLogger("evaluation").info(json.dumps(report, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()
