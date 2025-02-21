package com.gamee.devoot_backend.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamee.devoot_backend.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
