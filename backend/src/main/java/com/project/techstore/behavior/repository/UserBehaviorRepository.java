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
}
