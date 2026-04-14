package com.example.expbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetRequest {
    private String category;
    private Double limit;
    private Integer month;
    private Integer year;
}

/**
 * Budget Request DTO
 * This DTO is used when frontend wants to create or update a budget
 * Frontend sends JSON:
{
  "category": "Food",
  "limit": 5000,
  "month": 4,
  "year": 2026
} spring converts this json to javaobject using this DTO 
 full flow: DTO → Entity → DB → Response DTO 

 step : 2 request reaches spring (Dispatchservlet) and finds the controller like below:
 @PostMapping("/budget")
public String createBudget(@RequestBody BudgetRequest request)

step 3:
spring uses jackson to convert json to java object 

step 4: now method runs and we can access the data using request.getCategory() 
 */