package com.smartagriculture.smartagriculture.dto;

import java.math.BigDecimal;

/**
 * CropFinancialResponse is one row of the crop-wise table.
 * For example: Rice, income 36000, expenses 5500, profit 30500.
 * cropId is null for the special row "General (no crop)".
 */
public class CropFinancialResponse {

    private Long cropId;
    private String cropName;
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal profitLoss;
    // One of: "PROFIT", "LOSS", "BREAK_EVEN"
    private String status;

    public CropFinancialResponse(Long cropId, String cropName, BigDecimal totalIncome,
                                 BigDecimal totalExpenses, BigDecimal profitLoss, String status) {
        this.cropId = cropId;
        this.cropName = cropName;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.profitLoss = profitLoss;
        this.status = status;
    }

    // Only getters are needed to create the JSON.
    public Long getCropId() {
        return cropId;
    }

    public String getCropName() {
        return cropName;
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