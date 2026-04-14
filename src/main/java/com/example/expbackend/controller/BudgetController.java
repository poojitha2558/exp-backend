package com.example.expbackend.controller;

import com.example.expbackend.dto.BudgetRequest;
import com.example.expbackend.dto.BudgetResponse;
import com.example.expbackend.dto.BudgetStatusResponse;
import com.example.expbackend.entity.User;
import com.example.expbackend.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Budget Controller
 * Handles all budget-related endpoints
 */
@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@Tag(name = "Budgets", description = "APIs for managing budgets and budget tracking")
public class BudgetController {

    private final BudgetService budgetService;

    /**
     * Create a new budget
     */
    @PostMapping
    @Operation(summary = "Create budget")
    @ApiResponse(responseCode = "201", description = "Budget created successfully")
    public ResponseEntity<com.example.expbackend.dto.ApiResponse<BudgetResponse>> createBudget(
        @RequestBody BudgetRequest request,
        HttpServletRequest httpRequest
    ) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            User user = new User();
            user.setId(userId);

            BudgetResponse response = budgetService.createBudget(user, request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(com.example.expbackend.dto.ApiResponse.success(response, "Budget created successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(com.example.expbackend.dto.ApiResponse.error("INVALID_BUDGET", e.getMessage()));
        }
    }

    /**
     * Get all budgets for a month
     */
    @GetMapping
    @Operation(summary = "Get budgets")
    public ResponseEntity<com.example.expbackend.dto.ApiResponse<List<BudgetResponse>>> getBudgets(
        @Parameter(description = "Month (1-12), defaults to current month")
        @RequestParam(required = false) Integer month,
        @Parameter(description = "Year, defaults to current year")
        @RequestParam(required = false) Integer year,
        HttpServletRequest httpRequest
    ) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            User user = new User();
            user.setId(userId);

            List<BudgetResponse> budgets = budgetService.getBudgets(user, month, year);
            return ResponseEntity.ok(com.example.expbackend.dto.ApiResponse.success(budgets, "Budgets retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(com.example.expbackend.dto.ApiResponse.error("FETCH_ERROR", e.getMessage()));
        }
    }

    /**
     * Get single budget by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get budget by ID")
    public ResponseEntity<com.example.expbackend.dto.ApiResponse<BudgetResponse>> getBudgetById(
        @PathVariable Long id,
        HttpServletRequest httpRequest
    ) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            User user = new User();
            user.setId(userId);

            BudgetResponse response = budgetService.getBudgetById(id, user);
            return ResponseEntity.ok(com.example.expbackend.dto.ApiResponse.success(response, "Budget retrieved successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(com.example.expbackend.dto.ApiResponse.error("NOT_FOUND", e.getMessage()));
        }
    }

    /**
     * Update budget
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Update budget")
    public ResponseEntity<com.example.expbackend.dto.ApiResponse<BudgetResponse>> updateBudget(
        @PathVariable Long id,
        @RequestBody BudgetRequest request,
        HttpServletRequest httpRequest
    ) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            User user = new User();
            user.setId(userId);

            BudgetResponse response = budgetService.updateBudget(id, user, request);
            return ResponseEntity.ok(com.example.expbackend.dto.ApiResponse.success(response, "Budget updated successfully"));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(com.example.expbackend.dto.ApiResponse.error("NOT_FOUND", e.getMessage()));
            }
            return ResponseEntity
                    .badRequest()
                    .body(com.example.expbackend.dto.ApiResponse.error("INVALID_BUDGET", e.getMessage()));
        }
    }

    /**
     * Delete budget
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete budget")
    public ResponseEntity<com.example.expbackend.dto.ApiResponse<Void>> deleteBudget(
        @PathVariable Long id,
        HttpServletRequest httpRequest
    ) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            User user = new User();
            user.setId(userId);

            budgetService.deleteBudget(id, user);
            return ResponseEntity.ok(com.example.expbackend.dto.ApiResponse.success("Budget deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(com.example.expbackend.dto.ApiResponse.error("NOT_FOUND", e.getMessage()));
        }
    }

    /**
     * Get budget status with spending progress
     *
     * @param month Month (1-12), defaults to current month
     * @param year Year, defaults to current year
     * @return List of budget status with spent/remaining amounts and percentages
     *
     * Example:
     * GET /api/budgets/status?month=4&year=2026
     * Response: {
     *   "success": true,
     *   "data": [
     *     {
     *       "category": "food",
     *       "limit": 2000,
     *       "spent": 555,
     *       "remaining": 1445,
     *       "percentage": 27.75
     *     }
     *   ]
     * }
     */
    @GetMapping("/status/current")
    @Operation(summary = "Get budget status with spending progress")
    public ResponseEntity<com.example.expbackend.dto.ApiResponse<List<BudgetStatusResponse>>> getBudgetStatus(
        @Parameter(description = "Month (1-12), defaults to current month")
        @RequestParam(required = false) Integer month,
        @Parameter(description = "Year, defaults to current year")
        @RequestParam(required = false) Integer year,
        HttpServletRequest httpRequest
    ) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            User user = new User();
            user.setId(userId);

            List<BudgetStatusResponse> status = budgetService.getBudgetStatus(user, month, year);
            return ResponseEntity.ok(com.example.expbackend.dto.ApiResponse.success(status, "Budget status retrieved"));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(com.example.expbackend.dto.ApiResponse.error("FETCH_ERROR", e.getMessage()));
        }
    }
}

