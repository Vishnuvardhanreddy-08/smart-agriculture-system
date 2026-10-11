package com.smartagriculture.smartagriculture.controller;

import com.smartagriculture.smartagriculture.dto.CropFinancialResponse;
import com.smartagriculture.smartagriculture.dto.MonthlyFinancialResponse;
import com.smartagriculture.smartagriculture.dto.ProfitLossResponse;
import com.smartagriculture.smartagriculture.dto.TotalAmountResponse;
import com.smartagriculture.smartagriculture.service.FinancialService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * FinancialController gives money totals, crop-wise numbers and monthly reports for ONE farmer.
 * All endpoints only READ (GET). Nothing is created or changed here.
 */
// @RestController: handles REST requests and returns JSON.
@RestController
// @RequestMapping: all URLs here start with /api/financial.
@RequestMapping("/api/financial")
public class FinancialController {

    private final FinancialService financialService;

    public FinancialController(FinancialService financialService) {
        this.financialService = financialService;
    }

    // GET /api/financial/farmer/1/income -> total income of farmer 1
    @GetMapping("/farmer/{farmerId}/income")
    public TotalAmountResponse getTotalIncome(@PathVariable Long farmerId) {
        return financialService.getTotalIncome(farmerId);
    }

    // GET /api/financial/farmer/1/expenses -> total expenses of farmer 1
    @GetMapping("/farmer/{farmerId}/expenses")
    public TotalAmountResponse getTotalExpenses(@PathVariable Long farmerId) {
        return financialService.getTotalExpenses(farmerId);
    }

    // GET /api/financial/farmer/1/profit-loss -> overall profit/loss of farmer 1
    @GetMapping("/farmer/{farmerId}/profit-loss")
    public ProfitLossResponse getProfitLoss(@PathVariable Long farmerId) {
        return financialService.getProfitLoss(farmerId);
    }

    // NEW: GET /api/financial/farmer/1/crop-wise -> profit/loss of each crop of farmer 1
    @GetMapping("/farmer/{farmerId}/crop-wise")
    public List<CropFinancialResponse> getCropWise(@PathVariable Long farmerId) {
        return financialService.getCropWise(farmerId);
    }

    // NEW: GET /api/financial/farmer/1/monthly?year=2026 -> month-by-month report
    // @RequestParam reads "year" from the URL after the "?".
    // required = false means the year is optional (the current year is used if it is missing).
    @GetMapping("/farmer/{farmerId}/monthly")
    public List<MonthlyFinancialResponse> getMonthlyReport(
            @PathVariable Long farmerId,
            @RequestParam(required = false) Integer year) {
        return financialService.getMonthlyReport(farmerId, year);
    }
}