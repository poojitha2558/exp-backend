package com.example.expbackend.service;

import com.example.expbackend.dto.TransactionRequest;
import com.example.expbackend.dto.TransactionResponse;
import com.example.expbackend.entity.Expense;
import com.example.expbackend.entity.User;
import com.example.expbackend.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Transaction Service
 * Handles all transaction (expense/income) operations
 */
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final ExpenseRepository expenseRepository;

    /**
     * Create a new transaction
     */
    public TransactionResponse createTransaction(User user, TransactionRequest request) {
        // Validation
        validateTransaction(request);

        // Create expense entity
        Expense expense = new Expense();
        expense.setUser(user);
        expense.setType(request.getType());
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setNote(request.getNote());
        expense.setDate(request.getDate());
        expense.setCreatedAt(System.currentTimeMillis());
        expense.setUpdatedAt(System.currentTimeMillis());

        Expense saved = expenseRepository.save(expense);
        return mapToResponse(saved);
    }

    /**
     * Get all transactions for user with optional filters and pagination
     */
    public Page<TransactionResponse> getTransactions(User user, String type, String category, 
                                                     String month, Integer year, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);

        List<Expense> expenses;

        // Apply filters
        if (year != 0 && month != null && !month.isEmpty()) {
            String monthYear = String.format("%d-%s", year, padMonth(month));
            expenses = expenseRepository.findByUserAndMonth(user, monthYear + "%");
        } else if (category != null && !category.isEmpty()) {
            if (type != null && !type.isEmpty()) {
                expenses = expenseRepository.findByUserAndTypeAndCategory(user, type, category);
            } else {
                expenses = expenseRepository.findByUserAndCategory(user, category);
            }
        } else if (type != null && !type.isEmpty()) {
            expenses = expenseRepository.findByUserAndType(user, type);
        } else {
            expenses = expenseRepository.findByUser(user);
        }

        // Convert to responses and paginate manually
        List<TransactionResponse> responses = expenses.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        int start = Math.min(page * limit, responses.size());
        int end = Math.min((page + 1) * limit, responses.size());

        Page<TransactionResponse> result = new org.springframework.data.domain.PageImpl<>(
                responses.subList(start, end),
                pageable,
                responses.size()
        );

        return result;
    }

    /**
     * Get single transaction by ID
     */
    public TransactionResponse getTransactionById(Long id, User user) {
        Expense expense = expenseRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        return mapToResponse(expense);
    }

    /**
     * Update transaction
     */
    public TransactionResponse updateTransaction(Long id, User user, TransactionRequest request) {
        Expense expense = expenseRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        // Validate if provided
        validateTransaction(request);

        if (request.getType() != null) {
            expense.setType(request.getType());
        }
        if (request.getAmount() != null) {
            expense.setAmount(request.getAmount());
        }
        if (request.getCategory() != null) {
            expense.setCategory(request.getCategory());
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
     * Get recent transactions
     */
    public List<TransactionResponse> getRecentTransactions(User user, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return expenseRepository.findRecentByUser(user, pageable).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Validate transaction request
     */
    private void validateTransaction(TransactionRequest request) {
        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        if (request.getType() == null || request.getType().isEmpty()) {
            throw new IllegalArgumentException("Type is required (expense or income)");
        }
        if (!request.getType().equals("expense") && !request.getType().equals("income")) {
            throw new IllegalArgumentException("Type must be 'expense' or 'income'");
        }
        if (request.getCategory() == null || request.getCategory().isEmpty()) {
            throw new IllegalArgumentException("Category is required");
        }
        if (request.getDate() == null || request.getDate().isEmpty()) {
            throw new IllegalArgumentException("Date is required");
        }
        // Validate date format (YYYY-MM-DD)
        if (!request.getDate().matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Date must be in YYYY-MM-DD format");
        }
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

    /**
     * Pad month with leading zero if needed
     */
    private String padMonth(String month) {
        if (month.length() == 1) {
            return "0" + month;
        }
        return month;
    }
}
