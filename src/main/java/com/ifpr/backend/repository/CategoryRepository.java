package com.ifpr.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ifpr.backend.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUser_Id(Long userId);

}