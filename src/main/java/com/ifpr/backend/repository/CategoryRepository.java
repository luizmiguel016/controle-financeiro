package com.ifpr.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ifpr.backend.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUser_Id(Long userId);

    Optional<Category> findByIdAndUser_Id(Long id, Long userId);
}
