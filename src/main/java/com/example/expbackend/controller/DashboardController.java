package com.example.expbackend.controller;

import com.example.expbackend.dto.DashboardSummaryResponse;
import com.example.expbackend.dto.TransactionResponse;
import com.example.expbackend.entity.User;
import com.example.expbackend.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Dashboard Controller
 * Handles dashboard-related endpoints
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "APIs for dashboard data")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Get dashboard summary for a specific month/year
     *
     * @param month Month number (1-12), defaults to current month
     * @param year Year, defaults to current year
     * @return Summary with balance, income, expenses, top category
     *
     * Example:
     * GET /api/dashboard/summary?month=4&year=2026
     * Response: {
     *   "success": true,
     *   "data": {
     *     "balance": -555,
     *     "income": 0,
     *     "expenses": 555,
     *     "topCategory": {
     *       "name": "Food",
     *       "amount": 555
     *     }
     *   }
     * }
     */
    @GetMapping("/summary")
    @Operation(summary = "Get dashboard summary")
    @ApiResponse(responseCode = "200", description = "Summary retrieved successfully")
    public ResponseEntity<com.example.expbackend.dto.ApiResponse<DashboardSummaryResponse>> getSummary(
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

            DashboardSummaryResponse summary = dashboardService.getSummary(user, month, year);
            return ResponseEntity.ok(com.example.expbackend.dto.ApiResponse.success(summary, "Dashboard summary retrieved"));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(com.example.expbackend.dto.ApiResponse.error("FETCH_ERROR", e.getMessage()));
        }
    }

    /**
     * Get recent transactions
     *
     * @param limit Number of recent transactions to fetch (default: 10)
     * @return List of recent transactions
     *
     * Example:
     * GET /api/dashboard/recent
     * Response: {
     *   "success": true,
     *   "data": [
     *     {
     *       "id": 1,
     *       "type": "expense",
     *       "amount": 555,
     *       "category": "food",
     *       "note": "Dinner",
     *       "date": "2026-04-09",
     *       "createdAt": 1680518400000
     *     }
     *   ]
     * }
     */
    @GetMapping("/recent")
    @Operation(summary = "Get recent transactions")
    @ApiResponse(responseCode = "200", description = "Recent transactions retrieved successfully")
    public ResponseEntity<com.example.expbackend.dto.ApiResponse<List<TransactionResponse>>> getRecentTransactions(
        @Parameter(description = "Number of recent transactions to fetch")
        @RequestParam(required = false, defaultValue = "10") int limit,
        HttpServletRequest httpRequest
    ) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            User user = new User();
            user.setId(userId);

            List<TransactionResponse> recent = dashboardService.getRecentTransactions(user, limit);
            return ResponseEntity.ok(com.example.expbackend.dto.ApiResponse.success(recent, "Recent transactions retrieved"));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(com.example.expbackend.dto.ApiResponse.error("FETCH_ERROR", e.getMessage()));
        }
    }
}
