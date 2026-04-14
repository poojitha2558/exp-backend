package com.example.expbackend.controller;

import com.example.expbackend.entity.User;
import com.example.expbackend.service.FinancialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Dashboard/Insights Controller
 * Handles dashboard summary and financial insights
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "APIs for dashboard and financial insights")
public class FinancialAnalyticsController {

    private final FinancialService financialService;

    /**
     * Get dashboard summary for a month
     */
    @GetMapping("/summary")
    @Operation(summary = "Get dashboard summary")
    public ResponseEntity<com.example.expbackend.dto.ApiResponse<Map<String, Object>>> getDashboardSummary(
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

            Map<String, Object> summary = financialService.getDashboardSummary(user, month, year);
            return ResponseEntity.ok(com.example.expbackend.dto.ApiResponse.success(summary, "Dashboard summary retrieved"));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(com.example.expbackend.dto.ApiResponse.error("ERROR", e.getMessage()));
        }
    }

    /**
     * Get recent transactions
     */
    @GetMapping("/recent")
    @Operation(summary = "Get recent transactions")
    public ResponseEntity<com.example.expbackend.dto.ApiResponse<Object>> getRecentTransactions(
        @Parameter(description = "Limit (default 10)")
        @RequestParam(required = false) Integer limit,
        HttpServletRequest httpRequest
    ) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            User user = new User();
            user.setId(userId);

            return ResponseEntity.ok(com.example.expbackend.dto.ApiResponse.success(
                financialService.getRecentTransactions(user, limit),
                "Recent transactions retrieved"
            ));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(com.example.expbackend.dto.ApiResponse.error("ERROR", e.getMessage()));
        }
    }

    /**
     * Get monthly spending breakdown
     */
    @GetMapping("/monthly-spending")
    @Operation(summary = "Get monthly spending breakdown")
    public ResponseEntity<com.example.expbackend.dto.ApiResponse<Object>> getMonthlySpendings(
        @Parameter(description = "Year")
        @RequestParam(required = false) Integer year,
        HttpServletRequest httpRequest
    ) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            User user = new User();
            user.setId(userId);

            return ResponseEntity.ok(com.example.expbackend.dto.ApiResponse.success(
                financialService.getMonthlySpendings(user, year),
                "Monthly spending retrieved"
            ));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(com.example.expbackend.dto.ApiResponse.error("ERROR", e.getMessage()));
        }
    }

    /**
     * Get category breakdown for a month
     */
    @GetMapping("/category-breakdown")
    @Operation(summary = "Get spending by category")
    public ResponseEntity<com.example.expbackend.dto.ApiResponse<Object>> getCategoryBreakdown(
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

            return ResponseEntity.ok(com.example.expbackend.dto.ApiResponse.success(
                financialService.getCategoryBreakdown(user, month, year),
                "Category breakdown retrieved"
            ));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(com.example.expbackend.dto.ApiResponse.error("ERROR", e.getMessage()));
        }
    }

    /**
     * Get overall spending statistics
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get spending statistics")
    public ResponseEntity<com.example.expbackend.dto.ApiResponse<Object>> getSpendingStats(
        HttpServletRequest httpRequest
    ) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            User user = new User();
            user.setId(userId);

            return ResponseEntity.ok(com.example.expbackend.dto.ApiResponse.success(
                financialService.getSpendingStats(user),
                "Spending statistics retrieved"
            ));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(com.example.expbackend.dto.ApiResponse.error("ERROR", e.getMessage()));
        }
    }
}

