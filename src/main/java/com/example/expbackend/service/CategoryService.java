package com.example.expbackend.service;

import com.example.expbackend.entity.Category;
import com.example.expbackend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

     private final CategoryRepository categoryRepository; //why private ,final so cant b reassigned,  Encapsulation(OOP concept), Only this class should use it, Outside classes shouldn’t access directly

    /**
     * Create a new category
     */
    public Category createCategory(String name, String description, String icon, String type) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }

        // Check if category already exists
        Optional<Category> existingCategory = categoryRepository.findByName(name.trim());
        if (existingCategory.isPresent()) {
            throw new IllegalArgumentException("Category with this name already exists");
        }

        Category category = new Category();
        category.setName(name.trim());
        category.setDescription(description != null ? description.trim() : "");
        category.setIcon(icon);
        category.setType(type != null ? type : "expense");
        category.setCreatedAt(System.currentTimeMillis());
        category.setUpdatedAt(System.currentTimeMillis());

        return categoryRepository.save(category);
    }

    /**
     * Get all categories
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Get a specific category by id
     */
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    /**
     * Get a category by name
     */
    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    /**
     * Update a category
     */
    public Category updateCategory(Long id, String name, String description, String icon, String type) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (name != null && !name.trim().isEmpty()) {
            String newName = name.trim();
            // Check if another category with the same name exists
            Optional<Category> existingCategory = categoryRepository.findByName(newName);
            if (existingCategory.isPresent() && !existingCategory.get().getId().equals(id)) {
                throw new IllegalArgumentException("Category with this name already exists");
            }
            category.setName(newName);
        }

        if (description != null) {
            category.setDescription(description.trim());
        }

        if (icon != null) {
            category.setIcon(icon);
        }

        if (type != null) {
            category.setType(type);
        }

        category.setUpdatedAt(System.currentTimeMillis());
        return categoryRepository.save(category);
    }

    /**
     * Delete a category
     */
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Category not found");
        }
        categoryRepository.deleteById(id);
    }

    /**
     * Check if a category exists
     */
    public boolean categoryExists(Long id) {
        return categoryRepository.existsById(id);
    }
}

