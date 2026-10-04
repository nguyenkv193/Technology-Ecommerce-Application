package com.project.techstore.category.repository;

import com.project.techstore.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findBySlug(String slug);

    boolean existsBySlug(String slug);

    // Lấy các danh mục gốc (không có parent)
    List<Category> findByParentIsNullOrderByCreatedAtDesc();

    // Lấy các danh mục con theo parentId
    List<Category> findByParentIdOrderByCreatedAtDesc(Long parentId);
}
