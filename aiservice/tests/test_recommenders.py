import asyncio
from threading import Event, Thread
from time import monotonic
from unittest.mock import Mock
import pytest
import requests
from fastapi.testclient import TestClient
from app import main
from app.models.schemas import ProductFeatureItem, UserInteractionItem
from app.routers import recommendations
from app.services import data_loader
from app.services.content_based_recommender import ContentBasedRecommender


@pytest.fixture
def products():
    return [
        ProductFeatureItem(id=1, name="Phone A", slug="phone-a", categoryId=10, category="Phone",
                           brand="Apple", description="OLED",
                           attributes=[{"name": "CPU", "value": "A18"}, {"name": "RAM", "value": "8GB"}]),
        ProductFeatureItem(id=2, name="Phone B", slug="phone-b", categoryId=10, category="Phone",
                           brand="Apple", description="OLED",
                           attributes=[{"name": "CPU", "value": "A18"}, {"name": "RAM", "value": "8GB"}]),
        ProductFeatureItem(id=3, name="Laptop", slug="laptop", categoryId=20, category="Laptop",
                           brand="Dell", description="Notebook i7 SSD"),
        ProductFeatureItem(id=4, name="Phone C", slug="phone-c", categoryId=10, category="Phone",
                           brand="Samsung", description="AMOLED Snapdragon"),
    ]


@pytest.fixture
def state(monkeypatch, products):
    state = recommendations.ModelState()
    monkeypatch.setattr(recommendations, "model_state", state)
    monkeypatch.setattr(main, "model_state", state)
    monkeypatch.setattr(data_loader.DataLoader, "load_products", lambda: products)
    assert state.refresh()
    return state


@pytest.fixture
def client(state):
    # Explicitly built state; no startup worker or real network in API unit tests.
    return TestClient(main.app)


def test_similar_stays_in_category_and_never_returns_target(products):
    model = ContentBasedRecommender()
    model.fit(products)
    results = model.recommend_similar(1, 10)
    assert results[0].product_id == 2
    assert all(item.product_id in {2, 4} for item in results)
    assert model.recommend_similar(999) == []


def test_personalized_uses_specs_and_excludes_entire_history(products):
    model = ContentBasedRecommender()
    model.fit(products)
    history = [UserInteractionItem(product_id=1, score=5), UserInteractionItem(product_id=4, score=1)]
    results = model.recommend_for_user(history)
    assert results[0].product_id == 2
    assert all(item.product_id not in {1, 4} for item in results)
    assert model.recommend_for_user([UserInteractionItem(product_id=999, score=5)]) == []
    assert model.recommend_for_user([]) == []


def test_empty_catalog_resets_previous_model(products):
    model = ContentBasedRecommender()
    model.fit(products)
    model.fit([])
    assert model.recommend_similar(1) == []
    assert model.products == []


def test_refresh_failure_keeps_last_real_snapshot(state, monkeypatch):
    old_model = state.get_model()
    def fail():
        raise requests.ConnectionError("backend offline")
    monkeypatch.setattr(data_loader.DataLoader, "load_products", fail)
    assert state.refresh() is False
    assert state.get_model() is old_model
    assert state.status()["refresh_failed"]
    assert state.status()["data_source"] == "backend"


def test_refresh_publishes_only_complete_snapshot(state, monkeypatch):
    entered, finish = Event(), Event()
    old_model = state.get_model()
    def delayed():
        entered.set()
        assert finish.wait(2)
        return []
    monkeypatch.setattr(data_loader.DataLoader, "load_products", delayed)
    thread = Thread(target=state.refresh)
    thread.start()
    try:
        assert entered.wait(2)
        assert state.get_model() is old_model
        assert state.refresh() is False
    finally:
        finish.set()
        thread.join(2)
    assert not thread.is_alive()
    assert state.get_model() is not old_model
    assert state.status()["product_count"] == 0


def test_unready_and_expired_models_return_503(client, state):
    state.updated_monotonic = None
    assert client.get("/ready").status_code == 503
    assert client.get("/api/v1/recommend/similar/1").status_code == 503
    assert client.post("/api/v1/recommend/user", json={"interactions": []}).status_code == 503
    state.updated_monotonic = monotonic() - main.settings.max_model_age_seconds - 1
    assert client.get("/ready").status_code == 503


def test_health_reports_real_catalog_and_update_time(client):
    data = client.get("/health").json()
    assert data["ready"] is True
    assert data["product_count"] == 4
    assert data["data_source"] == "backend"
    assert data["last_updated_at"]
    assert client.get("/ready").status_code == 200


def test_personalized_api_receives_fresh_history_per_request(client):
    first = client.post("/api/v1/recommend/user", json={"interactions": [{"product_id": 1, "score": 5}], "limit": 4})
    assert first.status_code == 200
    assert first.json()["source"] == "content_based"
    assert 1 not in {item["product_id"] for item in first.json()["recommendations"]}
    second = client.post("/api/v1/recommend/user", json={"interactions": [{"product_id": 2, "score": 5}], "limit": 4})
    assert 2 not in {item["product_id"] for item in second.json()["recommendations"]}
    assert client.post("/api/v1/recommend/user", json={"interactions": []}).json()["recommendations"] == []


@pytest.mark.parametrize("score", [0, -1, "NaN", "Infinity"])
def test_invalid_interaction_scores_rejected(client, score):
    assert client.post("/api/v1/recommend/user", json={"interactions": [{"product_id": 1, "score": score}]}).status_code == 422


@pytest.mark.parametrize("path", ["/api/v1/recommend/popular", "/api/v1/analytics/clusters",
                                  "/api/v1/recommend/user/1"])
def test_retired_routes_removed(client, path):
    assert client.get(path).status_code == 404


def test_retired_evaluation_and_retrain_removed(client):
    assert client.post("/api/v1/recommend/evaluate").status_code == 404
    assert client.post("/api/v1/recommend/retrain").status_code == 404


def test_catalog_loader_paginates_and_preserves_description_and_specs(monkeypatch):
    feature = {"id": 1, "name": "Phone", "slug": "phone", "categoryId": 10,
               "category": "Phone", "brand": "Apple", "description": "Real description",
               "attributes": [{"id": 42, "name": "CPU", "value": "A18"}]}
    responses = [
        Mock(json=Mock(return_value={"success": True, "data": {"content": [feature], "last": False}})),
        Mock(json=Mock(return_value={"success": True, "data": {"content": [{**feature, "id": 2}], "last": True}})),
    ]
    get = Mock(side_effect=responses)
    monkeypatch.setattr(data_loader.requests, "get", get)
    loaded = data_loader.DataLoader.load_products()
    assert [p.id for p in loaded] == [1, 2]
    assert loaded[0].description == "Real description"
    assert loaded[0].attributes == [{"name": "CPU", "value": "A18"}]
    assert get.call_args_list[1].kwargs["params"]["page"] == 1


def test_empty_backend_is_not_replaced_by_mock(monkeypatch):
    monkeypatch.setattr(data_loader.requests, "get", Mock(
        return_value=Mock(json=Mock(return_value={"success": True, "data": {"content": [], "last": True}}))))
    assert data_loader.DataLoader.load_products() == []


def test_backend_failure_is_not_replaced_by_mock(monkeypatch):
    monkeypatch.setattr(data_loader.requests, "get", Mock(side_effect=requests.ConnectionError("offline")))
    with pytest.raises(requests.ConnectionError):
        data_loader.DataLoader.load_products()


def test_background_refresh_recovers_after_backend_startup_failure(state, monkeypatch, products):
    calls = []
    def load():
        calls.append(1)
        if len(calls) == 1:
            raise requests.ConnectionError("backend starting")
        return products
    monkeypatch.setattr(data_loader.DataLoader, "load_products", load)
    monkeypatch.setattr(main.settings, "retry_seconds", 0.01)
    async def run():
        task = asyncio.create_task(main.refresh_catalog())
        try:
            for _ in range(100):
                if len(calls) >= 2 and not state.status()["refresh_failed"]:
                    break
                await asyncio.sleep(0.005)
            assert len(calls) >= 2
            assert state.status()["ready"]
            assert not state.status()["refresh_failed"]
        finally:
            task.cancel()
            with pytest.raises(asyncio.CancelledError):
                await task
    asyncio.run(run())
