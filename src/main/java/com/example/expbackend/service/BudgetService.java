package com.example.expbackend.service;

import com.example.expbackend.dto.BudgetRequest;
import com.example.expbackend.dto.BudgetResponse;
import com.example.expbackend.dto.BudgetStatusResponse;
import com.example.expbackend.entity.Budget;
import com.example.expbackend.entity.User;
import com.example.expbackend.repository.BudgetRepository;
import com.example.expbackend.repository.ExpenseRepository;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Budget Service
 * Handles budget creation, updates, and status tracking
 */
@Data
@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;

    /**
     * Create a new budget
     */
    public BudgetResponse createBudget(User user, BudgetRequest request) {
        // Validation
        validateBudgetRequest(request);

        // Check for duplicate budget for same month/year/category
        if (budgetRepository.existsByUserAndCategoryAndMonthAndYear(
                user, request.getCategory(), request.getMonth(), request.getYear())) {
            throw new IllegalArgumentException("Budget already exists for this category in the given month");
        }

        // Create budget
        Budget budget = new Budget();
        budget.setUser(user);
        budget.setCategory(request.getCategory());
        budget.setLimit(request.getLimit());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());
        budget.setCreatedAt(System.currentTimeMillis());
        budget.setUpdatedAt(System.currentTimeMillis());

        Budget saved = budgetRepository.save(budget);
        return mapToResponse(saved);
    }

    /**
     * Get all budgets for user in a specific month
     */
    public List<BudgetResponse> getBudgets(User user, Integer month, Integer year) {
        // Get current month/year if not provided
        YearMonth yearMonth = YearMonth.now();
        if (month != null && year != null) {
            yearMonth = YearMonth.of(year, month);
        }

        List<Budget> budgets = budgetRepository.findByUserAndMonthAndYear(
                user, yearMonth.getMonthValue(), yearMonth.getYear());

        return budgets.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get budget by ID
     */
    public BudgetResponse getBudgetById(Long id, User user) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found"));

        // Verify ownership
        if (!budget.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Budget not found");
        }

        return mapToResponse(budget);
    }

    /**
     * Update budget
     */
    public BudgetResponse updateBudget(Long id, User user, BudgetRequest request) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found"));

        // Verify ownership
        if (!budget.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Budget not found");
        }

        // Validate new limit if provided
        if (request.getLimit() != null && request.getLimit() <= 0) {
            throw new IllegalArgumentException("Budget limit must be greater than 0");
        }

        if (request.getLimit() != null) {
            budget.setLimit(request.getLimit());
        }

        budget.setUpdatedAt(System.currentTimeMillis());
        Budget updated = budgetRepository.save(budget);
        return mapToResponse(updated);
    }

    /**
     * Delete budget
     */
    public void deleteBudget(Long id, User user) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found"));

        // Verify ownership
        if (!budget.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Budget not found");
        }

        budgetRepository.delete(budget);
    }

    /**
     * Get budget status with spending information
     */
    public List<BudgetStatusResponse> getBudgetStatus(User user, Integer month, Integer year) {
        // Get current month/year if not provided
        YearMonth yearMonth = YearMonth.now();
        if (month != null && year != null) {
            yearMonth = YearMonth.of(year, month);
        }

        // Get all budgets for the month
        List<Budget> budgets = budgetRepository.findByUserAndMonthAndYear(
                user, yearMonth.getMonthValue(), yearMonth.getYear());

        // Create date filter pattern (YYYY-MM)
        String monthYearPattern = String.format("%04d-%02d", yearMonth.getYear(), yearMonth.getMonthValue());

        return budgets.stream()
                .map(budget -> {
                    // Calculate spent amount for this category in this month
                    Double spent = expenseRepository.sumByUserAndCategoryAndTypeAndMonth(
                            user, budget.getCategory(), "expense", monthYearPattern + "%");

                    Double remaining = budget.getLimit() - spent;
                    Double percentage = (spent / budget.getLimit()) * 100;

                    return BudgetStatusResponse.builder()
                            .category(budget.getCategory())
                            .limit(budget.getLimit())
                            .spent(spent)
                            .remaining(Math.max(remaining, 0.0))
                            .percentage(Math.min(percentage, 100.0))
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Validate budget request
     */
    private void validateBudgetRequest(BudgetRequest request) {
        if (request.getLimit() == null || request.getLimit() <= 0) {
            throw new IllegalArgumentException("Budget limit must be greater than 0");
        }
        if (request.getCategory() == null || request.getCategory().isEmpty()) {
            throw new IllegalArgumentException("Category is required");
        }
        if (request.getMonth() == null || request.getMonth() < 1 || request.getMonth() > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        if (request.getYear() == null || request.getYear() < 2020) {
            throw new IllegalArgumentException("Year must be valid");
        }
    }

    /**
     * Map Budget entity to BudgetResponse DTO
     */
    private BudgetResponse mapToResponse(Budget budget) {
        return BudgetResponse.builder()
                .id(budget.getId())
                .category(budget.getCategory())
                .limit(budget.getLimit())
                .month(budget.getMonth())
                .year(budget.getYear())
                .createdAt(budget.getCreatedAt())
                .build();
    }


}

