package com.smartagriculture.smartagriculture.dto;

import java.math.BigDecimal;

/**
 * ProfitLossResponse shows one farmer's overall financial position.
 * profitLoss = totalIncome - totalExpenses (negative means loss).
 * These values are CALCULATED, never stored in the database.
 */
public class ProfitLossResponse {

    private Long farmerId;
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal profitLoss;
    // One of: "PROFIT", "LOSS", "BREAK_EVEN"
    private String status;

    public ProfitLossResponse(Long farmerId, BigDecimal totalIncome, BigDecimal totalExpenses,
                              BigDecimal profitLoss, String status) {
        this.farmerId = farmerId;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.profitLoss = profitLoss;
        this.status = status;
    }

    // Only getters are needed to create the JSON.
    public Long getFarmerId() {
        return farmerId;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public BigDecimal getProfitLoss() {
        return profitLoss;
    }

    public String getStatus() {
        return status;
    }
}