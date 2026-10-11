package com.smartagriculture.smartagriculture.controller;

import com.smartagriculture.smartagriculture.dto.ExpenseRequest;
import com.smartagriculture.smartagriculture.dto.ExpenseResponse;
import com.smartagriculture.smartagriculture.security.CurrentFarmer;
import com.smartagriculture.smartagriculture.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * ExpenseController handles HTTP requests for expenses.
 * A farmerId in the URL is checked automatically by FarmerAccessInterceptor.
 * A farmerId inside the JSON body is checked here with CurrentFarmer.requireSame.
 */
// @RestController: handles REST requests and returns JSON.
@RestController
// @RequestMapping: all URLs here start with /api/expenses.
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    // GET /api/expenses/farmer/1
    //     /api/expenses/farmer/1?cropId=1&from=2026-10-01&to=2026-10-31&search=fert
    // The farmer id is part of the path. The filters are OPTIONAL query parameters.
    @GetMapping("/farmer/{farmerId}")
    public List<ExpenseResponse> getExpensesByFarmer(
            @PathVariable Long farmerId,
            @RequestParam(required = false) Long cropId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String search) {
        return expenseService.getExpensesByFarmer(farmerId, cropId, from, to, search);
    }

    // GET /api/expenses/5?farmerId=1 -> expense 5, only if it belongs to farmer 1
    @GetMapping("/{id}")
    public ExpenseResponse getExpense(@PathVariable Long id, @RequestParam Long farmerId) {
        return expenseService.getExpense(id, farmerId);
    }

    // POST /api/expenses -> create an expense (JSON body, validated by @Valid)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse createExpense(@Valid @RequestBody ExpenseRequest request) {
        // The farmerId in the body must be the logged-in farmer's own id.
        CurrentFarmer.requireSame(request.getFarmerId());
        return expenseService.createExpense(request);
    }

    // PUT /api/expenses/5 -> update expense 5 (farmerId is in the body)
    @PutMapping("/{id}")
    public ExpenseResponse updateExpense(@PathVariable Long id, @Valid @RequestBody ExpenseRequest request) {
        CurrentFarmer.requireSame(request.getFarmerId());
        return expenseService.updateExpense(id, request);
    }

    // DELETE /api/expenses/5?farmerId=1 -> delete expense 5, only if it belongs to farmer 1
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExpense(@PathVariable Long id, @RequestParam Long farmerId) {
        expenseService.deleteExpense(id, farmerId);
    }
}