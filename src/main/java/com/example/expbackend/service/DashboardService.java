package com.example.expbackend.service;

import com.example.expbackend.dto.DashboardSummaryResponse;
import com.example.expbackend.dto.TransactionResponse;
import com.example.expbackend.entity.Expense;
import com.example.expbackend.entity.User;
import com.example.expbackend.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dashboard Service
 * Handles dashboard summary and recent transactions
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ExpenseRepository expenseRepository;

    /**
     * Get dashboard summary for current month
     */
    public DashboardSummaryResponse getSummary(User user, Integer month, Integer year) {
        // Get current month/year if not provided
        YearMonth yearMonth = YearMonth.now();
        if (month != null && year != null) {
            yearMonth = YearMonth.of(year, month);
        }

        // Create date filter pattern (YYYY-MM)
        String monthYearPattern = String.format("%04d-%02d", yearMonth.getYear(), yearMonth.getMonthValue());

        // Calculate totals
        Double income = expenseRepository.sumByUserAndTypeAndMonth(user, "income", monthYearPattern + "%");
        Double expenses = expenseRepository.sumByUserAndTypeAndMonth(user, "expense", monthYearPattern + "%");
        Double balance = income - expenses;

        // Find top spending category
        List<Expense> allExpenses = expenseRepository.findByUserAndMonth(user, monthYearPattern + "%");
        DashboardSummaryResponse.CategorySpending topCategory = findTopCategory(allExpenses);

        return DashboardSummaryResponse.builder()
                .balance(balance)
                .income(income)
                .expenses(expenses)
                .topCategory(topCategory)
                .build();
    }

    /**
     * Get recent transactions
     */
    public List<TransactionResponse> getRecentTransactions(User user, int limit) {
        return expenseRepository.findRecentByUser(user, org.springframework.data.domain.PageRequest.of(0, limit))
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Find top spending category from expenses
     */
    private DashboardSummaryResponse.CategorySpending findTopCategory(List<Expense> expenses) {
        return expenses.stream()
                .filter(e -> "expense".equals(e.getType()))
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ))
                .entrySet()
                .stream()
                .max((e1, e2) -> Double.compare(e1.getValue(), e2.getValue()))
                .map(entry -> DashboardSummaryResponse.CategorySpending.builder()
                        .name(entry.getKey())
                        .amount(entry.getValue())
                        .build())
                .orElse(null);
    }

    /**
     * Map Expense entity to TransactionResponse DTO
     */
    private TransactionResponse mapToResponse(Expense expense) {
        return TransactionResponse.builder()
                .id(expense.getId())
                .type(expense.getType())
                .amount(expense.getAmount())
                .category(expense.getCategory())
                .note(expense.getNote())
                .date(expense.getDate())
                .createdAt(expense.getCreatedAt())
                .build();
    }
}
