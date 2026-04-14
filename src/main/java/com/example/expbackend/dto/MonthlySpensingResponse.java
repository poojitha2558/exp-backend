package com.example.expbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Monthly Spending Insight DTO (for graphs)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySpensingResponse {
    private String month;
    private Double total;
}
