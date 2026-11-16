package com.project.techstore.banner.repository;

import com.project.techstore.banner.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

    @Query("SELECT b FROM Banner b WHERE b.position = :position AND b.status = 'ACTIVE' " +
           "AND (b.startDate IS NULL OR b.startDate <= :now) " +
           "AND (b.endDate IS NULL OR b.endDate >= :now) " +
           "ORDER BY b.displayOrder ASC, b.createdAt DESC")
    List<Banner> findActiveBannersByPosition(@Param("position") String position, @Param("now") Instant now);

    List<Banner> findByPositionOrderByDisplayOrderAsc(String position);

    List<Banner> findAllByOrderByPositionAscDisplayOrderAsc();

    @Modifying
    @Query("UPDATE Banner b SET b.clickCount = b.clickCount + 1 WHERE b.id = :id")
    void incrementClickCount(@Param("id") Long id);
}
