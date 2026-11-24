package com.project.techstore.behavior.repository;

import com.project.techstore.behavior.entity.BehaviorType;
import com.project.techstore.behavior.entity.UserBehavior;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface UserBehaviorRepository extends JpaRepository<UserBehavior, Long> {

    String INTERACTION_SCORE_EXPRESSION = "SUM(CASE b.actionType " +
            "WHEN com.project.techstore.behavior.entity.BehaviorType.VIEW THEN 1.0 " +
            "WHEN com.project.techstore.behavior.entity.BehaviorType.ADD_TO_CART THEN 3.0 " +
            "WHEN com.project.techstore.behavior.entity.BehaviorType.WISHLIST THEN 4.0 " +
            "WHEN com.project.techstore.behavior.entity.BehaviorType.PURCHASE THEN 5.0 " +
            "WHEN com.project.techstore.behavior.entity.BehaviorType.RATING THEN " +
            "CASE b.actionValue WHEN 'stars=5' THEN 2.0 WHEN 'stars=4' THEN 1.0 ELSE 0.0 END " +
            "ELSE 0.0 END)";

    @EntityGraph(attributePaths = {"product"})
    List<UserBehavior> findTop50ByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT b.product.id, COUNT(b) FROM UserBehavior b WHERE b.actionType = :actionType AND b.product IS NOT NULL GROUP BY b.product.id ORDER BY COUNT(b) DESC")
    List<Object[]> findTopProductsByAction(@Param("actionType") BehaviorType actionType, Pageable pageable);

    @Query("SELECT b.user.id, b.product.id, " + INTERACTION_SCORE_EXPRESSION + ", " +
           "COUNT(b), MAX(b.createdAt) " +
           "FROM UserBehavior b " +
           "WHERE b.user.id = :userId AND b.product IS NOT NULL " +
           "GROUP BY b.user.id, b.product.id " +
           "HAVING " + INTERACTION_SCORE_EXPRESSION + " > 0")
    List<Object[]> aggregateUserProductInteractions(@Param("userId") Long userId);

    @Query("SELECT b.user.id, b.product.id, " + INTERACTION_SCORE_EXPRESSION + ", " +
           "COUNT(b), MAX(b.createdAt) " +
           "FROM UserBehavior b " +
           "WHERE b.user IS NOT NULL AND b.product IS NOT NULL AND b.createdAt <= :asOf " +
           "GROUP BY b.user.id, b.product.id " +
           "HAVING " + INTERACTION_SCORE_EXPRESSION + " > 0 " +
           "ORDER BY MAX(b.createdAt) DESC, b.user.id ASC, b.product.id ASC")
    List<Object[]> aggregateTrainingInteractions(@Param("asOf") Instant asOf, Pageable pageable);

    @Query("SELECT b.actionValue, COUNT(b) FROM UserBehavior b " +
           "WHERE b.actionType = com.project.techstore.behavior.entity.BehaviorType.SEARCH " +
           "AND b.actionValue IS NOT NULL AND TRIM(b.actionValue) != '' " +
           "AND b.createdAt >= :since " +
           "GROUP BY b.actionValue " +
           "ORDER BY COUNT(b) DESC, MAX(b.createdAt) DESC")
    List<Object[]> findTopSearchKeywordsSince(@Param("since") Instant since, Pageable pageable);

    @Query("SELECT b.actionValue, COUNT(b) FROM UserBehavior b " +
           "WHERE b.actionType = com.project.techstore.behavior.entity.BehaviorType.SEARCH " +
           "AND b.actionValue IS NOT NULL AND TRIM(b.actionValue) != '' " +
           "GROUP BY b.actionValue " +
           "ORDER BY COUNT(b) DESC, MAX(b.createdAt) DESC")
    List<Object[]> findAllTimeTopSearchKeywords(Pageable pageable);

    @Query("SELECT b.actionType, COUNT(b) FROM UserBehavior b " +
           "WHERE b.createdAt >= :since " +
           "GROUP BY b.actionType")
    List<Object[]> countByActionTypeSince(@Param("since") Instant since);

    @Query(value = "SELECT TO_CHAR(created_at AT TIME ZONE 'Asia/Ho_Chi_Minh', 'YYYY-MM-DD') AS day_str, " +
                   "action_type, COUNT(*) AS cnt " +
                   "FROM user_behaviors " +
                   "WHERE created_at >= :since " +
                   "GROUP BY day_str, action_type " +
                   "ORDER BY day_str ASC", nativeQuery = true)
    List<Object[]> findDailyActionCountsSince(@Param("since") Instant since);

    @Query("SELECT b.product.id, " +
           "SUM(CASE WHEN b.actionType = com.project.techstore.behavior.entity.BehaviorType.VIEW THEN 1L ELSE 0L END), " +
           "SUM(CASE WHEN b.actionType = com.project.techstore.behavior.entity.BehaviorType.WISHLIST THEN 1L ELSE 0L END), " +
           "SUM(CASE WHEN b.actionType = com.project.techstore.behavior.entity.BehaviorType.ADD_TO_CART THEN 1L ELSE 0L END), " +
           "SUM(CASE WHEN b.actionType = com.project.techstore.behavior.entity.BehaviorType.PURCHASE THEN 1L ELSE 0L END), " +
           INTERACTION_SCORE_EXPRESSION + " " +
           "FROM UserBehavior b " +
           "WHERE b.product IS NOT NULL AND b.createdAt >= :since " +
           "GROUP BY b.product.id " +
           "ORDER BY " + INTERACTION_SCORE_EXPRESSION + " DESC, " +
           "SUM(CASE WHEN b.actionType = com.project.techstore.behavior.entity.BehaviorType.VIEW THEN 1L ELSE 0L END) DESC")
    List<Object[]> findTopInteractedProductsSince(@Param("since") Instant since, Pageable pageable);

    @Query("SELECT b.actionType, COUNT(b) FROM UserBehavior b " +
           "WHERE b.createdAt >= :since AND b.createdAt < :until " +
           "GROUP BY b.actionType")
    List<Object[]> countByActionTypeBetween(@Param("since") Instant since, @Param("until") Instant until);

    @Query(value = "SELECT TO_CHAR(created_at AT TIME ZONE 'Asia/Ho_Chi_Minh', 'YYYY-MM-DD') AS day_str, " +
                   "action_type, COUNT(*) AS cnt " +
                   "FROM user_behaviors " +
                   "WHERE created_at >= :since AND created_at < :until " +
                   "GROUP BY day_str, action_type " +
                   "ORDER BY day_str ASC", nativeQuery = true)
    List<Object[]> findDailyActionCountsBetween(@Param("since") Instant since, @Param("until") Instant until);

    @Query("SELECT b.product.id, " +
           "SUM(CASE WHEN b.actionType = com.project.techstore.behavior.entity.BehaviorType.VIEW THEN 1L ELSE 0L END), " +
           "SUM(CASE WHEN b.actionType = com.project.techstore.behavior.entity.BehaviorType.WISHLIST THEN 1L ELSE 0L END), " +
           "SUM(CASE WHEN b.actionType = com.project.techstore.behavior.entity.BehaviorType.ADD_TO_CART THEN 1L ELSE 0L END), " +
           "SUM(CASE WHEN b.actionType = com.project.techstore.behavior.entity.BehaviorType.PURCHASE THEN 1L ELSE 0L END), " +
           INTERACTION_SCORE_EXPRESSION + " " +
           "FROM UserBehavior b " +
           "WHERE b.product IS NOT NULL AND b.createdAt >= :since AND b.createdAt < :until " +
           "GROUP BY b.product.id " +
           "ORDER BY " + INTERACTION_SCORE_EXPRESSION + " DESC, " +
           "SUM(CASE WHEN b.actionType = com.project.techstore.behavior.entity.BehaviorType.VIEW THEN 1L ELSE 0L END) DESC")
    List<Object[]> findTopInteractedProductsBetween(
            @Param("since") Instant since,
            @Param("until") Instant until,
            Pageable pageable
    );
}
