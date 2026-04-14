package com.example.expbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create Transaction Request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    private String type; // "expense" or "income"
    private Double amount;
    private String category;
    private String note;
    private String date; // "2026-04-09"
}
