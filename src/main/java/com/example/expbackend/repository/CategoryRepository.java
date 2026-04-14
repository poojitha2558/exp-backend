package com.example.expbackend.repository;

import com.example.expbackend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find a category by name
     * @param name category name
     * @return Optional containing category if found
     */
    Optional<Category> findByName(String name);
}

