package com.example.expbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Budget Status DTO - Shows progress against budget limit
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetStatusResponse {
    private String category;
    private Double limit;
    private Double spent;
    private Double remaining;
    private Double percentage; // 0-100
}
