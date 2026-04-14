package com.example.expbackend.service;

import com.example.expbackend.dto.TransactionRequest;
import com.example.expbackend.dto.TransactionResponse;
import com.example.expbackend.entity.Expense;
import com.example.expbackend.entity.User;
import com.example.expbackend.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Transaction Service
 * Handles CRUD operations and business logic for transactions (expenses and income)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    /**
     * Create a new transaction
     */
    public TransactionResponse createTransaction(User user, TransactionRequest request) {
        // Validation
        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        if (request.getCategory() == null || request.getCategory().isEmpty()) {
            throw new IllegalArgumentException("Category is required");
        }
        if (request.getType() == null || (!request.getType().equals("expense") && !request.getType().equals("income"))) {
            throw new IllegalArgumentException("Type must be 'expense' or 'income'");
        }

        // Create expense entity
        Expense expense = new Expense();
        expense.setUser(user);
        expense.setType(request.getType());
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setNote(request.getNote() != null ? request.getNote() : "");
        expense.setDate(request.getDate() != null ? request.getDate() : LocalDate.now().toString());
        expense.setCreatedAt(System.currentTimeMillis());
        expense.setUpdatedAt(System.currentTimeMillis());

        Expense saved = expenseRepository.save(expense);
        return mapToResponse(saved);
    }

    /**
     * Get all transactions for user with filters
     */
    public List<TransactionResponse> getTransactions(User user, String type, String category, Integer month, Integer year) {
        List<Expense> expenses;

        if (type != null && category != null && month != null && year != null) {
            String monthYear = String.format("%04d-%02d", year, month);
            expenses = expenseRepository.findByUserAndTypeAndCategory(user, type, category);
            expenses = filterByMonth(expenses, monthYear);
        } else if (type != null && month != null && year != null) {
            String monthYear = String.format("%04d-%02d", year, month);
            expenses = expenseRepository.findByUserAndType(user, type);
            expenses = filterByMonth(expenses, monthYear);
        } else if (category != null && month != null && year != null) {
            String monthYear = String.format("%04d-%02d", year, month);
            expenses = expenseRepository.findByUserAndCategory(user, category);
            expenses = filterByMonth(expenses, monthYear);
        } else if (type != null && category != null) {
            expenses = expenseRepository.findByUserAndTypeAndCategory(user, type, category);
        } else if (type != null) {
            expenses = expenseRepository.findByUserAndType(user, type);
        } else if (category != null) {
            expenses = expenseRepository.findByUserAndCategory(user, category);
        } else if (month != null && year != null) {
            String monthYear = String.format("%04d-%02d", year, month);
            expenses = expenseRepository.findByUser(user);
            expenses = filterByMonth(expenses, monthYear);
        } else {
            expenses = expenseRepository.findByUser(user);
        }

        return expenses.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Get transaction by id
     */
    public TransactionResponse getTransactionById(Long id, User user) {
        Expense expense = expenseRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        return mapToResponse(expense);
    }

    /**
     * Get recent transactions
     */
    public List<TransactionResponse> getRecentTransactions(User user, Integer limit) {
        int pageSize = limit != null && limit > 0 ? limit : 10;
        List<Expense> expenses = expenseRepository.findByUser(user);
        return expenses.stream()
                .limit(pageSize)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update transaction
     */
    public TransactionResponse updateTransaction(Long id, User user, TransactionRequest request) {
        Expense expense = expenseRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        if (request.getAmount() != null && request.getAmount() > 0) {
            expense.setAmount(request.getAmount());
        }
        if (request.getCategory() != null && !request.getCategory().isEmpty()) {
            expense.setCategory(request.getCategory());
        }
        if (request.getType() != null && (request.getType().equals("expense") || request.getType().equals("income"))) {
            expense.setType(request.getType());
        }
        if (request.getNote() != null) {
            expense.setNote(request.getNote());
        }
        if (request.getDate() != null) {
            expense.setDate(request.getDate());
        }

        expense.setUpdatedAt(System.currentTimeMillis());
        Expense updated = expenseRepository.save(expense);
        return mapToResponse(updated);
    }

    /**
     * Delete transaction
     */
    public void deleteTransaction(Long id, User user) {
        Expense expense = expenseRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        expenseRepository.delete(expense);
    }

    /**
     * Helper method to filter expenses by month
     */
    private List<Expense> filterByMonth(List<Expense> expenses, String monthYear) {
        return expenses.stream()
                .filter(e -> e.getDate().startsWith(monthYear))
                .collect(Collectors.toList());
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

