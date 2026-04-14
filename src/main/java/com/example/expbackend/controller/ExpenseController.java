package com.example.expbackend.controller;

import com.example.expbackend.dto.TransactionRequest;
import com.example.expbackend.dto.TransactionResponse;
import com.example.expbackend.entity.User;
import com.example.expbackend.service.ExpenseService;
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
 * Transaction Controller
 * Handles all transaction (expense/income) endpoints
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "APIs for managing transactions (expenses and income)")
public class ExpenseController {

    private final ExpenseService expenseService;

    /**
     * Create a new transaction
     */
    @PostMapping
    @Operation(summary = "Create transaction")
    @ApiResponse(responseCode = "201", description = "Transaction created successfully")
    public ResponseEntity<com.example.expbackend.dto.ApiResponse<TransactionResponse>> createTransaction(
        @RequestBody TransactionRequest request,
        HttpServletRequest httpRequest
    ) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            User user = new User();
            user.setId(userId);

            TransactionResponse response = expenseService.createTransaction(user, request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(com.example.expbackend.dto.ApiResponse.success(response, "Transaction created successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(com.example.expbackend.dto.ApiResponse.error("INVALID_TRANSACTION", e.getMessage()));
        }
    }

    /**
     * Get transactions with optional filtering
     */
    @GetMapping
    @Operation(summary = "Get transactions")
    public ResponseEntity<com.example.expbackend.dto.ApiResponse<List<TransactionResponse>>> getTransactions(
        @Parameter(description = "Transaction type (expense|income)")
        @RequestParam(required = false) String type,
        @Parameter(description = "Category filter")
        @RequestParam(required = false) String category,
        @Parameter(description = "Month (1-12)")
        @RequestParam(required = false) Integer month,
        @Parameter(description = "Year")
        @RequestParam(required = false) Integer year,
        HttpServletRequest httpRequest
    ) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            User user = new User();
            user.setId(userId);

            List<TransactionResponse> transactions = expenseService.getTransactions(user, type, category, month, year);
            return ResponseEntity.ok(com.example.expbackend.dto.ApiResponse.success(transactions, "Transactions retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(com.example.expbackend.dto.ApiResponse.error("FETCH_ERROR", e.getMessage()));
        }
    }

    /**
     * Get transaction by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID")
    public ResponseEntity<com.example.expbackend.dto.ApiResponse<TransactionResponse>> getTransactionById(
        @PathVariable Long id,
        HttpServletRequest httpRequest
    ) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            User user = new User();
            user.setId(userId);

            TransactionResponse response = expenseService.getTransactionById(id, user);
            return ResponseEntity.ok(com.example.expbackend.dto.ApiResponse.success(response, "Transaction retrieved successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(com.example.expbackend.dto.ApiResponse.error("NOT_FOUND", e.getMessage()));
        }
    }

    /**
     * Get recent transactions
     */
    @GetMapping("/recent")
    @Operation(summary = "Get recent transactions")
    public ResponseEntity<com.example.expbackend.dto.ApiResponse<List<TransactionResponse>>> getRecentTransactions(
        @Parameter(description = "Limit (default 10)")
        @RequestParam(required = false) Integer limit,
        HttpServletRequest httpRequest
    ) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            User user = new User();
            user.setId(userId);

            List<TransactionResponse> transactions = expenseService.getRecentTransactions(user, limit);
            return ResponseEntity.ok(com.example.expbackend.dto.ApiResponse.success(transactions, "Recent transactions retrieved"));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(com.example.expbackend.dto.ApiResponse.error("FETCH_ERROR", e.getMessage()));
        }
    }

    /**
     * Update transaction
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Update transaction")
    public ResponseEntity<com.example.expbackend.dto.ApiResponse<TransactionResponse>> updateTransaction(
        @PathVariable Long id,
        @RequestBody TransactionRequest request,
        HttpServletRequest httpRequest
    ) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            User user = new User();
            user.setId(userId);

            TransactionResponse response = expenseService.updateTransaction(id, user, request);
            return ResponseEntity.ok(com.example.expbackend.dto.ApiResponse.success(response, "Transaction updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(com.example.expbackend.dto.ApiResponse.error("NOT_FOUND", e.getMessage()));
        }
    }

    /**
     * Delete transaction
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete transaction")
    public ResponseEntity<com.example.expbackend.dto.ApiResponse<Void>> deleteTransaction(
        @PathVariable Long id,
        HttpServletRequest httpRequest
    ) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            User user = new User();
            user.setId(userId);

            expenseService.deleteTransaction(id, user);
            return ResponseEntity.ok(com.example.expbackend.dto.ApiResponse.success("Transaction deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(com.example.expbackend.dto.ApiResponse.error("NOT_FOUND", e.getMessage()));
        }
    }
}

