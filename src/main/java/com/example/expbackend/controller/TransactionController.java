package com.example.expbackend.controller;

import com.example.expbackend.dto.TransactionRequest;
import com.example.expbackend.dto.TransactionResponse;
import com.example.expbackend.entity.User;
import com.example.expbackend.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Transaction Controller
 * Handles all transaction (expense/income) endpoints
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "APIs for managing transactions (expenses and income)")
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Create a new transaction
     */
    @PostMapping
    @Operation(summary = "Create transaction")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Transaction created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid transaction data")
    })
    public ResponseEntity<com.example.expbackend.dto.ApiResponse<TransactionResponse>> createTransaction(
        @RequestBody TransactionRequest request,
        HttpServletRequest httpRequest
    ) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            User user = new User();
            user.setId(userId);

            TransactionResponse response = transactionService.createTransaction(user, request);
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
     * Get all transactions with filters and pagination
     */
    @GetMapping
    @Operation(summary = "Get transactions")
    public ResponseEntity<com.example.expbackend.dto.ApiResponse<Page<TransactionResponse>>> getTransactions(
        @Parameter(description = "Filter by type: expense or income")
        @RequestParam(required = false) String type,
        @Parameter(description = "Filter by category name")
        @RequestParam(required = false) String category,
        @Parameter(description = "Filter by month (1-12)")
        @RequestParam(required = false) String month,
        @Parameter(description = "Filter by year")
        @RequestParam(required = false, defaultValue = "0") Integer year,
        @Parameter(description = "Page number (0-based)")
        @RequestParam(required = false, defaultValue = "0") int page,
        @Parameter(description = "Items per page")
        @RequestParam(required = false, defaultValue = "10") int limit,
        HttpServletRequest httpRequest
    ) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            User user = new User();
            user.setId(userId);

            Page<TransactionResponse> transactions = transactionService.getTransactions(
                    user, type, category, month, year, page, limit);

            return ResponseEntity.ok(com.example.expbackend.dto.ApiResponse.success(
                    transactions, "Transactions retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(com.example.expbackend.dto.ApiResponse.error("FETCH_ERROR", e.getMessage()));
        }
    }

    /**
     * Get single transaction by ID
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

            TransactionResponse response = transactionService.getTransactionById(id, user);
            return ResponseEntity.ok(com.example.expbackend.dto.ApiResponse.success(
                    response, "Transaction retrieved successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(com.example.expbackend.dto.ApiResponse.error("NOT_FOUND", e.getMessage()));
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

            TransactionResponse response = transactionService.updateTransaction(id, user, request);
            return ResponseEntity.ok(com.example.expbackend.dto.ApiResponse.success(
                    response, "Transaction updated successfully"));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(com.example.expbackend.dto.ApiResponse.error("NOT_FOUND", e.getMessage()));
            }
            return ResponseEntity
                    .badRequest()
                    .body(com.example.expbackend.dto.ApiResponse.error("INVALID_TRANSACTION", e.getMessage()));
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

            transactionService.deleteTransaction(id, user);
            return ResponseEntity.ok(com.example.expbackend.dto.ApiResponse.success(
                    "Transaction deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(com.example.expbackend.dto.ApiResponse.error("NOT_FOUND", e.getMessage()));
        }
    }
}
