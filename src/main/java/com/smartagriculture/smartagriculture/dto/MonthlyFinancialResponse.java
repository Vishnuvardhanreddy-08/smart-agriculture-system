package com.smartagriculture.smartagriculture.dto;

import java.math.BigDecimal;

/**
 * MonthlyFinancialResponse is one month of the monthly report.
 * For example: month 3 (March), income 10000, expenses 2000, profit 8000.
 */
public class MonthlyFinancialResponse {

    private int month;
    private String monthName;
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal profitLoss;

    public MonthlyFinancialResponse(int month, String monthName, BigDecimal totalIncome,
                                    BigDecimal totalExpenses, BigDecimal profitLoss) {
        this.month = month;
        this.monthName = monthName;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.profitLoss = profitLoss;
    }

    // Only getters are needed to create the JSON.
    public int getMonth() {
        return month;
    }

    public String getMonthName() {
        return monthName;
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
}