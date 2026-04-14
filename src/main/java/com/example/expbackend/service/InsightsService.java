package com.example.expbackend.service;

import com.example.expbackend.dto.CategoryBreakdownResponse;
import com.example.expbackend.dto.MonthlySpensingResponse;
import com.example.expbackend.entity.Expense;
import com.example.expbackend.entity.User;
import com.example.expbackend.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Insights Service
 * Handles financial insights for graphs and analytics
 */
@Service
@RequiredArgsConstructor
public class InsightsService {

    private final ExpenseRepository expenseRepository;
    private static final String[] MONTH_NAMES = {
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    /**
     * Get monthly spending for the year
     */
    public List<MonthlySpensingResponse> getMonthlySpending(User user, Integer year) {
        if (year == null) {
            year = LocalDate.now().getYear();
        }

        List<MonthlySpensingResponse> result = new ArrayList<>();
        final int finalYear = year;

        // Get all expenses for the year
        List<Expense> allExpenses = expenseRepository.findByUser(user);
        
        for (int month = 1; month <= 12; month++) {
            String monthYear = String.format("%04d-%02d", finalYear, month);
            
            Double total = allExpenses.stream()
                    .filter(e -> e.getDate().startsWith(monthYear) && "expense".equals(e.getType()))
                    .mapToDouble(Expense::getAmount)
                    .sum();

            result.add(MonthlySpensingResponse.builder()
                    .month(MONTH_NAMES[month - 1])
                    .total(total)
                    .build());
        }

        return result;
    }

    /**
     * Get category breakdown for a specific month
     */
    public List<CategoryBreakdownResponse> getCategoryBreakdown(User user, Integer month, Integer year) {
        // Get current month/year if not provided
        YearMonth yearMonth = YearMonth.now();
        if (month != null && year != null) {
            yearMonth = YearMonth.of(year, month);
        }

        // Create date filter pattern (YYYY-MM)
        String monthYearPattern = String.format("%04d-%02d", yearMonth.getYear(), yearMonth.getMonthValue());

        // Get all expenses for the month
        List<Expense> expenses = expenseRepository.findByUserAndMonth(user, monthYearPattern + "%");

        // Group by category and sum amounts
        return expenses.stream()
                .filter(e -> "expense".equals(e.getType()))
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ))
                .entrySet()
                .stream()
                .map(entry -> CategoryBreakdownResponse.builder()
                        .category(entry.getKey())
                        .amount(entry.getValue())
                        .build())
                .sorted(Comparator.comparingDouble(CategoryBreakdownResponse::getAmount).reversed())
                .collect(Collectors.toList());
    }
}
