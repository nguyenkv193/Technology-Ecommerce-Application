import pandas as pd
from typing import List, Dict, Any
from sklearn.cluster import KMeans
from sklearn.preprocessing import StandardScaler
from app.models.schemas import UserClusterItem, ClusterAnalysisResponse

class UserClusteringService:
    """
    Phân cụm người dùng bằng thuật toán K-Means (User Segmentation).
    Phân tích hành vi tương tác và mức độ gắn bó của khách hàng để phục vụ
    tiếp thị cá nhân hóa và báo cáo quản trị viên (Admin Dashboard).
    """

    def __init__(self, n_clusters: int = 3):
        self.n_clusters = n_clusters
        self.kmeans = None
        self.scaler = StandardScaler()
        self.user_clusters: List[UserClusterItem] = []
        self.cluster_summaries: List[Dict[str, Any]] = []

    def fit(self, interactions_df: pd.DataFrame) -> ClusterAnalysisResponse:
        if interactions_df is None or interactions_df.empty or len(interactions_df['user_id'].unique()) < 2:
            return ClusterAnalysisResponse(total_clusters=0, clusters=[], user_assignments=[])

        # Trích xuất đặc trưng (Feature Engineering) cho từng User
        user_features = interactions_df.groupby('user_id').agg(
            total_interactions=('interaction_count', 'sum'),
            total_score=('score', 'sum'),
            avg_score=('score', 'mean'),
            distinct_products=('product_id', 'nunique')
        ).reset_index()

        num_users = len(user_features)
        k = min(self.n_clusters, num_users)
        if k < 1:
            k = 1

        feature_cols = ['total_interactions', 'total_score', 'avg_score', 'distinct_products']
        X = self.scaler.fit_transform(user_features[feature_cols])

        self.kmeans = KMeans(n_clusters=k, random_state=42, n_init='auto')
        user_features['cluster'] = self.kmeans.fit_predict(X)

        # Định nghĩa nhãn chân dung khách hàng (User Persona Mapping)
        cluster_means = user_features.groupby('cluster')['total_score'].mean().to_dict()
        sorted_clusters = sorted(cluster_means.items(), key=lambda x: x[1])

        persona_names = {}
        for rank, (cid, _) in enumerate(sorted_clusters):
            if rank == len(sorted_clusters) - 1:
                persona_names[cid] = "VIP & Khách hàng Đam mê Công nghệ (Tech Enthusiast)"
            elif rank == 0:
                persona_names[cid] = "Khách hàng Tiềm năng Mới (Casual Shopper)"
            else:
                persona_names[cid] = "Khách hàng Thường Xuyên (Engaged Consumer)"

        self.user_clusters = []
        for _, row in user_features.iterrows():
            cid = int(row['cluster'])
            self.user_clusters.append(UserClusterItem(
                user_id=int(row['user_id']),
                cluster_id=cid,
                persona_name=persona_names.get(cid, f"Nhóm {cid}"),
                avg_score=round(float(row['avg_score']), 2),
                total_interactions=int(row['total_interactions'])
            ))

        self.cluster_summaries = []
        for cid in range(k):
            members = user_features[user_features['cluster'] == cid]
            self.cluster_summaries.append({
                "cluster_id": cid,
                "persona_name": persona_names.get(cid, f"Nhóm {cid}"),
                "member_count": int(len(members)),
                "avg_total_score": round(float(members['total_score'].mean()), 2),
                "avg_interactions": round(float(members['total_interactions'].mean()), 1)
            })

        return ClusterAnalysisResponse(
            total_clusters=k,
            clusters=self.cluster_summaries,
            user_assignments=self.user_clusters
        )
