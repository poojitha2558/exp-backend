package com.example.expbackend.service;

import com.example.expbackend.entity.Expense;
import com.example.expbackend.entity.User;
import com.example.expbackend.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Dashboard/Insights Service
 * Handles dashboard summary, recent transactions, and financial insights
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FinancialService {

    private final ExpenseRepository expenseRepository;

    /**
     * Get dashboard summary for a specific month
     */
    public Map<String, Object> getDashboardSummary(User user, Integer month, Integer year) {
        YearMonth yearMonth = YearMonth.now();
        if (month != null && year != null) {
            yearMonth = YearMonth.of(year, month);
        }

        String monthYear = String.format("%04d-%02d", yearMonth.getYear(), yearMonth.getMonthValue());
        List<Expense> monthExpenses = expenseRepository.findByUser(user).stream()
                .filter(e -> e.getDate().startsWith(monthYear))
                .collect(Collectors.toList());

        Double income = monthExpenses.stream()
                .filter(e -> "income".equals(e.getType()))
                .mapToDouble(Expense::getAmount)
                .sum();

        Double expenses = monthExpenses.stream()
                .filter(e -> "expense".equals(e.getType()))
                .mapToDouble(Expense::getAmount)
                .sum();

        Double balance = income - expenses;

        // Find top expense category
        String topCategory = monthExpenses.stream()
                .filter(e -> "expense".equals(e.getType()))
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ))
                .entrySet().stream()
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse("N/A");

        Double topCategoryAmount = monthExpenses.stream()
                .filter(e -> "expense".equals(e.getType()) && e.getCategory().equals(topCategory))
                .mapToDouble(Expense::getAmount)
                .sum();

        Map<String, Object> summary = new HashMap<>();
        summary.put("month", yearMonth.getMonthValue());
        summary.put("year", yearMonth.getYear());
        summary.put("income", income);
        summary.put("expenses", expenses);
        summary.put("balance", balance);
        summary.put("topExpenseCategory", topCategory);
        summary.put("topCategoryAmount", topCategoryAmount);

        return summary;
    }

    /**
     * Get recent transactions
     */
    public List<Map<String, Object>> getRecentTransactions(User user, Integer limit) {
        int pageSize = limit != null && limit > 0 ? limit : 10;
        
        return expenseRepository.findByUser(user).stream()
                .sorted(Comparator.comparing(Expense::getDate).reversed())
                .limit(pageSize)
                .map(expense -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", expense.getId());
                    map.put("type", expense.getType());
                    map.put("amount", expense.getAmount());
                    map.put("category", expense.getCategory());
                    map.put("note", expense.getNote());
                    map.put("date", expense.getDate());
                    return map;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get monthly spending breakdown
     */
    public Map<String, Double> getMonthlySpendings(User user, Integer year) {
        int targetYear = year != null ? year : YearMonth.now().getYear();
        Map<String, Double> monthlySpendings = new LinkedHashMap<>();

        // Initialize all months
        for (int month = 1; month <= 12; month++) {
            String monthKey = String.format("%04d-%02d", targetYear, month);
            monthlySpendings.put(monthKey, 0.0);
        }

        // Calculate spending for each month
        List<Expense> allExpenses = expenseRepository.findByUser(user);
        for (Expense expense : allExpenses) {
            if ("expense".equals(expense.getType()) && expense.getDate().startsWith(String.valueOf(targetYear))) {
                String monthKey = expense.getDate().substring(0, 7); // YYYY-MM
                monthlySpendings.put(monthKey, monthlySpendings.getOrDefault(monthKey, 0.0) + expense.getAmount());
            }
        }

        return monthlySpendings;
    }

    /**
     * Get category breakdown for a month
     */
    public Map<String, Double> getCategoryBreakdown(User user, Integer month, Integer year) {
        YearMonth yearMonth = YearMonth.now();
        if (month != null && year != null) {
            yearMonth = YearMonth.of(year, month);
        }

        String monthYear = String.format("%04d-%02d", yearMonth.getYear(), yearMonth.getMonthValue());
        
        return expenseRepository.findByUser(user).stream()
                .filter(e -> "expense".equals(e.getType()) && e.getDate().startsWith(monthYear))
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ));
    }

    /**
     * Get total spending stats
     */
    public Map<String, Object> getSpendingStats(User user) {
        List<Expense> allExpenses = expenseRepository.findByUser(user);

        Double totalExpenses = allExpenses.stream()
                .filter(e -> "expense".equals(e.getType()))
                .mapToDouble(Expense::getAmount)
                .sum();

        Double totalIncome = allExpenses.stream()
                .filter(e -> "income".equals(e.getType()))
                .mapToDouble(Expense::getAmount)
                .sum();

        Double averageExpense = allExpenses.stream()
                .filter(e -> "expense".equals(e.getType()))
                .mapToDouble(Expense::getAmount)
                .average()
                .orElse(0.0);

        Double highestExpense = allExpenses.stream()
                .filter(e -> "expense".equals(e.getType()))
                .mapToDouble(Expense::getAmount)
                .max()
                .orElse(0.0);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalExpenses", totalExpenses);
        stats.put("totalIncome", totalIncome);
        stats.put("balance", totalIncome - totalExpenses);
        stats.put("averageExpense", averageExpense);
        stats.put("highestExpense", highestExpense);
        stats.put("transactionCount", allExpenses.size());

        return stats;
    }
}

