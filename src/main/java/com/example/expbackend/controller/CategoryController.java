package com.example.expbackend.controller;

import com.example.expbackend.entity.Category;
import com.example.expbackend.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Category Controller
 * Handles all category-related HTTP endpoints (CRUD operations)
 *
 * Real-World Analogy:
 * Think of categories like drawers in a filing cabinet.
 * You organize your expenses into drawers like "Food", "Transport", "Entertainment".
 * Once you create these drawers, you can put expenses in them.
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "APIs for managing expense categories")
public class CategoryController {

    private final CategoryService categoryService;

    // ==================== CREATE ENDPOINTS ====================

    /**
     * Create a new category for the user
     *
     * @param user current authenticated user
     * @param name the category name (must be unique per user)
     * @param description optional category description
     * @return created category
     *
     * Example:
     * POST /api/categories
     * Body: {
     *   "name": "Food",
     *   "description": "All food and restaurant expenses"
     * }
     * Response: {
     *   "message": "Category created successfully",
     *   "category": { id: 1, name: "Food", description: "...", ... }
     * }
     */
    @PostMapping
    @Operation(
        summary = "Create new category",
        description = "Create a new expense category"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Category created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid category data or category already exists"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Map<String, Object>> createCategory(
        @Parameter(description = "Category name")
        @RequestParam String name,
        @Parameter(description = "Optional category description")
        @RequestParam(required = false) String description,
        @Parameter(description = "Category icon (emoji)")
        @RequestParam(required = false) String icon,
        @Parameter(description = "Category type (expense|income)")
        @RequestParam(required = false, defaultValue = "expense") String type
    ) {
        try {
            Category category = categoryService.createCategory(name, description, icon, type);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Category created successfully");
            response.put("category", category);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                Map.of("error", e.getMessage())
            );
        }
    }
    /**
     * Get all categories for the current user
     *
     * @param user current authenticated user
     * @return list of all user's categories
     *
     * Example:
     * GET /api/categories
     * Response: {
     *   "categories": [
     *     { "id": 1, "name": "Food", "description": "..." },
     *     { "id": 2, "name": "Transport", "description": "..." }
     *   ],
     *   "totalCount": 2
     * }
     */
    @GetMapping
    @Operation(
        summary = "Get all categories",
        description = "Retrieve all available expense categories"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all categories")
    public ResponseEntity<Map<String, Object>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();

        Map<String, Object> response = new HashMap<>();
        response.put("categories", categories);
        response.put("totalCount", categories.size());

        return ResponseEntity.ok(response);
    }

    /**
     * Get a specific category by ID
     *
     * @param id the category ID
     * @param user current authenticated user
     * @return category details if found
     *
     * Example:
     * GET /api/categories/1
     * Response: {
     *   "category": {
     *     "id": 1,
     *     "name": "Food",
     *     "description": "All food and restaurant expenses",
     *     "userId": 1,
     *     "createdAt": 1680518400000,
     *     "updatedAt": 1680518400000
     *   }
     * }
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get category by ID",
        description = "Retrieve a specific category by its ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Category found"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<Map<String, Object>> getCategoryById(
        @Parameter(description = "Category ID")
        @PathVariable Long id
    ) {
        return categoryService.getCategoryById(id)
            .map(category -> {
                Map<String, Object> response = new HashMap<>();
                response.put("category", category);
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get category by name
     *
     * @param name the category name
     * @param user current authenticated user
     * @return category details if found
     *
     * Example:
     * GET /api/categories/by-name?name=Food
     * Response: {
     *   "category": { ... }
     * }
     */
    @GetMapping("/by-name")
    @Operation(
        summary = "Get category by name",
        description = "Retrieve a category by its name"
    )
    public ResponseEntity<Map<String, Object>> getCategoryByName(
        @Parameter(description = "Category name")
        @RequestParam String name
    ) {
        return categoryService.getCategoryByName(name)
            .map(category -> {
                Map<String, Object> response = new HashMap<>();
                response.put("category", category);
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("error", "Category not found")
            ));
    }

    // ==================== UPDATE ENDPOINTS ====================
    /**
     * Update an existing category
     * Can update name and/or description
     *
     * @param id the category ID
     * @param user current authenticated user
     * @param name new category name (optional)
     * @param description new description (optional)
     * @return updated category
     *
     * Example:
     * PUT /api/categories/1
     * Body: {
     *   "description": "Updated description"
     * }
     * Response: {
     *   "message": "Category updated successfully",
     *   "category": { updated category data }
     * }
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Update category",
        description = "Update an existing category"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Category updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid data or duplicate name"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<Map<String, Object>> updateCategory(
        @Parameter(description = "Category ID")
        @PathVariable Long id,
        @Parameter(description = "New category name (optional)")
        @RequestParam(required = false) String name,
        @Parameter(description = "New category description (optional)")
        @RequestParam(required = false) String description,
        @Parameter(description = "New category icon (optional)")
        @RequestParam(required = false) String icon,
        @Parameter(description = "New category type (optional)")
        @RequestParam(required = false) String type
    ) {
        try {
            Category updatedCategory = categoryService.updateCategory(id, name, description, icon, type);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Category updated successfully");
            response.put("category", updatedCategory);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("error", e.getMessage())
                );
            }
            return ResponseEntity.badRequest().body(
                Map.of("error", e.getMessage())
            );
        }
    }

    // ==================== DELETE ENDPOINTS ====================

    /**
     * Delete a category
     *
     * @param id the category ID
     * @param user current authenticated user
     * @return success message
     *
     * Example:
     * DELETE /api/categories/1
     * Response: {
     *   "message": "Category deleted successfully",
     *   "deletedCategoryId": 1
     * }
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete category",
        description = "Delete a category record"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Category deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<Map<String, Object>> deleteCategory(
        @Parameter(description = "Category ID")
        @PathVariable Long id
    ) {
        try {
            categoryService.deleteCategory(id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Category deleted successfully");
            response.put("deletedCategoryId", id);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("error", e.getMessage())
            );
        }
    }

    // ==================== VALIDATION ENDPOINTS ====================

    /**
     * Check if category name is available (not already used)
     * Useful for form validation before submitting
     *
     * @param name the category name to check
     * @param user current authenticated user
     * @return whether name is available
     *
     * Example:
     * GET /api/categories/check-name?name=Food
     * Response: {
     *   "available": true,
     *   "name": "Food"
     * }
     */
    @GetMapping("/check-name")
    @Operation(
        summary = "Check category name availability",
        description = "Check if a category name is available"
    )
    public ResponseEntity<Map<String, Object>> checkNameAvailability(
        @Parameter(description = "Category name to check")
        @RequestParam String name
    ) {
        boolean available = categoryService.getCategoryByName(name).isEmpty();

        Map<String, Object> response = new HashMap<>();
        response.put("available", available);
        response.put("name", name);

        return ResponseEntity.ok(response);
    }

    /**
     * Check if category exists and belongs to user
     *
     * @param id the category ID
     * @param user current authenticated user
     * @return whether category exists
     */
    @GetMapping("/{id}/exists")
    @Operation(
        summary = "Check category existence",
        description = "Check if a category exists"
    )
    public ResponseEntity<Map<String, Object>> categoryExists(
        @Parameter(description = "Category ID")
        @PathVariable Long id
    ) {
        boolean exists = categoryService.categoryExists(id);

        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        response.put("categoryId", id);

        return ResponseEntity.ok(response);
    }
}

