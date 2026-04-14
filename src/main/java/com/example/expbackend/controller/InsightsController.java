package com.example.expbackend.controller;

import com.example.expbackend.dto.CategoryBreakdownResponse;
import com.example.expbackend.dto.MonthlySpensingResponse;
import com.example.expbackend.entity.User;
import com.example.expbackend.service.InsightsService;
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
 * Insights Controller
 * Handles financial insights endpoints for analytics and visualizations
 */
@RestController
@RequestMapping("/api/insights")
@RequiredArgsConstructor
@Tag(name = "Insights", description = "APIs for financial insights and analytics")
public class InsightsController {

    private final InsightsService insightsService;

    /**
     * Get monthly spending for the year (for graph visualization)
     *
     * @param year Year for which to fetch data (defaults to current year)
     * @return List of monthly spending data
     *
     * Example:
     * GET /api/insights/monthly?year=2026
     * Response: {
     *   "success": true,
     *   "data": [
     *     { "month": "Jan", "total": 200 },
     *     { "month": "Feb", "total": 400 }
     *   ]
     * }
     */
    @GetMapping("/monthly")
    @Operation(summary = "Get monthly spending data")
    @ApiResponse(responseCode = "200", description = "Monthly data retrieved successfully")
    public ResponseEntity<com.example.expbackend.dto.ApiResponse<List<MonthlySpensingResponse>>> getMonthlySpending(
        @Parameter(description = "Year for which to fetch data (defaults to current year)")
        @RequestParam(required = false) Integer year,
        HttpServletRequest httpRequest
    ) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            User user = new User();
            user.setId(userId);

            List<MonthlySpensingResponse> data = insightsService.getMonthlySpending(user, year);
            return ResponseEntity.ok(com.example.expbackend.dto.ApiResponse.success(data, "Monthly spending retrieved"));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(com.example.expbackend.dto.ApiResponse.error("FETCH_ERROR", e.getMessage()));
        }
    }

    /**
     * Get category breakdown for a month (for pie chart visualization)
     *
     * @param month Month number (1-12), defaults to current month
     * @param year Year, defaults to current year
     * @return List of category spending breakdown
     *
     * Example:
     * GET /api/insights/category?month=4&year=2026
     * Response: {
     *   "success": true,
     *   "data": [
     *     { "category": "Food", "amount": 555 },
     *     { "category": "Transport", "amount": 200 }
     *   ]
     * }
     */
    @GetMapping("/category")
    @Operation(summary = "Get category breakdown")
    @ApiResponse(responseCode = "200", description = "Category data retrieved successfully")
    public ResponseEntity<com.example.expbackend.dto.ApiResponse<List<CategoryBreakdownResponse>>> getCategoryBreakdown(
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

            List<CategoryBreakdownResponse> data = insightsService.getCategoryBreakdown(user, month, year);
            return ResponseEntity.ok(com.example.expbackend.dto.ApiResponse.success(data, "Category breakdown retrieved"));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(com.example.expbackend.dto.ApiResponse.error("FETCH_ERROR", e.getMessage()));
        }
    }
}
