package com.smartagriculture.smartagriculture.service;

import com.smartagriculture.smartagriculture.dto.CropFinancialResponse;
import com.smartagriculture.smartagriculture.dto.CropTotal;
import com.smartagriculture.smartagriculture.dto.MonthlyFinancialResponse;
import com.smartagriculture.smartagriculture.dto.ProfitLossResponse;
import com.smartagriculture.smartagriculture.dto.TotalAmountResponse;
import com.smartagriculture.smartagriculture.model.Crop;
import com.smartagriculture.smartagriculture.model.Expense;
import com.smartagriculture.smartagriculture.model.Income;
import com.smartagriculture.smartagriculture.repository.CropRepository;
import com.smartagriculture.smartagriculture.repository.ExpenseRepository;
import com.smartagriculture.smartagriculture.repository.IncomeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.smartagriculture.smartagriculture.exception.ApiException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * FinancialService calculates money numbers for ONE farmer.
 * Nothing here is saved. Every number is calculated when requested.
 * Profit/Loss = Total Income - Total Expenses
 */
// @Service tells Spring this class contains business logic.
@Service
public class FinancialService {

    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final CropRepository cropRepository;
    private final FarmerService farmerService;

    // Constructor injection: Spring provides these objects.
    public FinancialService(IncomeRepository incomeRepository,
                            ExpenseRepository expenseRepository,
                            CropRepository cropRepository,
                            FarmerService farmerService) {
        this.incomeRepository = incomeRepository;
        this.expenseRepository = expenseRepository;
        this.cropRepository = cropRepository;
        this.farmerService = farmerService;
    }

    // ---------- Totals (from Step 7) ----------

    // Total income of ONE farmer.
    public TotalAmountResponse getTotalIncome(Long farmerId) {
        farmerService.getFarmerById(farmerId); // 404 if the farmer does not exist
        return new TotalAmountResponse(farmerId, orZero(incomeRepository.sumAmountByFarmerId(farmerId)));
    }

    // Total expenses of ONE farmer.
    public TotalAmountResponse getTotalExpenses(Long farmerId) {
        farmerService.getFarmerById(farmerId); // 404 if the farmer does not exist
        return new TotalAmountResponse(farmerId, orZero(expenseRepository.sumAmountByFarmerId(farmerId)));
    }

    // Profit or loss of ONE farmer.
    public ProfitLossResponse getProfitLoss(Long farmerId) {
        farmerService.getFarmerById(farmerId); // 404 if the farmer does not exist

        BigDecimal totalIncome = orZero(incomeRepository.sumAmountByFarmerId(farmerId));
        BigDecimal totalExpenses = orZero(expenseRepository.sumAmountByFarmerId(farmerId));
        BigDecimal profitLoss = totalIncome.subtract(totalExpenses);

        return new ProfitLossResponse(farmerId, totalIncome, totalExpenses, profitLoss, statusOf(profitLoss));
    }

    // ---------- Crop-wise (NEW) ----------

    // Income, expenses and profit/loss for each crop of ONE farmer.
    public List<CropFinancialResponse> getCropWise(Long farmerId) {
        farmerService.getFarmerById(farmerId); // 404 if the farmer does not exist

        // Two small maps: cropId -> total income, cropId -> total expenses.
        Map<Long, BigDecimal> incomeByCrop = toMap(incomeRepository.sumAmountByCrop(farmerId));
        Map<Long, BigDecimal> expenseByCrop = toMap(expenseRepository.sumAmountByCrop(farmerId));

        List<CropFinancialResponse> result = new ArrayList<>();

        // One row for every crop this farmer owns (crops with no records get zeros).
        for (Crop crop : cropRepository.findByFarmerId(farmerId)) {
            result.add(buildCropRow(crop.getId(), crop.getName(),
                    incomeByCrop.get(crop.getId()), expenseByCrop.get(crop.getId())));
        }

        // One extra row for money that is not linked to any crop (key = null).
        // This makes the crop rows add up to the overall profit/loss.
        if (incomeByCrop.containsKey(null) || expenseByCrop.containsKey(null)) {
            result.add(buildCropRow(null, "General (no crop)",
                    incomeByCrop.get(null), expenseByCrop.get(null)));
        }

        return result;
    }

    // ---------- Monthly report (NEW) ----------

    // Income, expenses and profit/loss for each of the 12 months of a year, for ONE farmer.
    // If year is null, the current year is used.
    public List<MonthlyFinancialResponse> getMonthlyReport(Long farmerId, Integer year) {
        farmerService.getFarmerById(farmerId); // 404 if the farmer does not exist

        int reportYear = (year == null) ? LocalDate.now().getYear() : year;
        if (reportYear < 2000 || reportYear > 2100) {
            // ResponseStatusException returns an HTTP error with our message (400 here).
            throw new ApiException(HttpStatus.BAD_REQUEST, "year.outOfRange");
        }

        LocalDate start = LocalDate.of(reportYear, 1, 1);
        LocalDate end = LocalDate.of(reportYear, 12, 31);

        // Arrays with 13 slots. We use slots 1 to 12 for the months (slot 0 is unused).
        BigDecimal[] incomeByMonth = newMonthlyTotals();
        BigDecimal[] expenseByMonth = newMonthlyTotals();

        // Add each income record into its month.
        for (Income income : incomeRepository.findByFarmerIdAndDateBetween(farmerId, start, end)) {
            int month = income.getDate().getMonthValue();
            incomeByMonth[month] = incomeByMonth[month].add(income.getAmount());
        }

        // Add each expense record into its month.
        for (Expense expense : expenseRepository.findByFarmerIdAndDateBetween(farmerId, start, end)) {
            int month = expense.getDate().getMonthValue();
            expenseByMonth[month] = expenseByMonth[month].add(expense.getAmount());
        }

        // Build the 12 rows, January to December.
        List<MonthlyFinancialResponse> result = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            result.add(new MonthlyFinancialResponse(
                    month,
                    Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                    incomeByMonth[month],
                    expenseByMonth[month],
                    incomeByMonth[month].subtract(expenseByMonth[month])));
        }
        return result;
    }

    // ---------- Helpers ----------

    // Helper: build one crop-wise row from (possibly null) income and expense totals.
    private CropFinancialResponse buildCropRow(Long cropId, String cropName,
                                               BigDecimal income, BigDecimal expenses) {
        BigDecimal totalIncome = orZero(income);
        BigDecimal totalExpenses = orZero(expenses);
        BigDecimal profitLoss = totalIncome.subtract(totalExpenses);
        return new CropFinancialResponse(cropId, cropName, totalIncome, totalExpenses,
                profitLoss, statusOf(profitLoss));
    }

    // Helper: turn the query result list into a map (cropId -> total).
    // HashMap allows a null key, which we use for "no crop".
    private Map<Long, BigDecimal> toMap(List<CropTotal> totals) {
        Map<Long, BigDecimal> map = new HashMap<>();
        for (CropTotal total : totals) {
            map.put(total.getCropId(), total.getTotal());
        }
        return map;
    }

    // Helper: an array of 13 zeros (0.00), one slot for each month (1 to 12).
    private BigDecimal[] newMonthlyTotals() {
        BigDecimal[] totals = new BigDecimal[13];
        Arrays.fill(totals, BigDecimal.ZERO.setScale(2));
        return totals;
    }

    // Helper: PROFIT, LOSS or BREAK_EVEN. compareTo compares with zero.
    private String statusOf(BigDecimal profitLoss) {
        int sign = profitLoss.compareTo(BigDecimal.ZERO);
        return sign > 0 ? "PROFIT" : (sign < 0 ? "LOSS" : "BREAK_EVEN");
    }

    // Helper: if the value is null (no rows), return 0.00.
    private BigDecimal orZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(2) : value;
    }
}