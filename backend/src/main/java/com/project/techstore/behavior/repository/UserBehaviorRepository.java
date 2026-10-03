package com.project.techstore.behavior.repository;

import com.project.techstore.behavior.entity.BehaviorType;
import com.project.techstore.behavior.entity.UserBehavior;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBehaviorRepository extends JpaRepository<UserBehavior, Long> {

    @EntityGraph(attributePaths = {"product"})
    List<UserBehavior> findTop50ByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT b.product.id, COUNT(b) FROM UserBehavior b WHERE b.actionType = :actionType AND b.product IS NOT NULL GROUP BY b.product.id ORDER BY COUNT(b) DESC")
    List<Object[]> findTopProductsByAction(@Param("actionType") BehaviorType actionType, Pageable pageable);

    @Query("SELECT b.user.id, b.product.id, " +
           "SUM(CASE b.actionType " +
           "WHEN com.project.techstore.behavior.entity.BehaviorType.VIEW THEN 1.0 " +
           "WHEN com.project.techstore.behavior.entity.BehaviorType.SEARCH THEN 1.5 " +
           "WHEN com.project.techstore.behavior.entity.BehaviorType.ADD_TO_CART THEN 2.5 " +
           "WHEN com.project.techstore.behavior.entity.BehaviorType.WISHLIST THEN 3.0 " +
           "WHEN com.project.techstore.behavior.entity.BehaviorType.PURCHASE THEN 5.0 " +
           "WHEN com.project.techstore.behavior.entity.BehaviorType.RATING THEN 4.0 " +
           "ELSE 1.0 END), " +
           "COUNT(b), MAX(b.createdAt) " +
           "FROM UserBehavior b " +
            "WHERE b.user.id = :userId AND b.product IS NOT NULL " +
            "GROUP BY b.user.id, b.product.id")
    List<Object[]> aggregateUserProductInteractions(@Param("userId") Long userId);
}
