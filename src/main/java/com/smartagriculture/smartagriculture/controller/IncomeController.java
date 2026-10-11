package com.smartagriculture.smartagriculture.controller;

import com.smartagriculture.smartagriculture.dto.IncomeRequest;
import com.smartagriculture.smartagriculture.dto.IncomeResponse;
import com.smartagriculture.smartagriculture.security.CurrentFarmer;
import com.smartagriculture.smartagriculture.service.IncomeService;
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
 * IncomeController handles HTTP requests for income.
 * A farmerId in the URL is checked automatically by FarmerAccessInterceptor.
 * A farmerId inside the JSON body is checked here with CurrentFarmer.requireSame.
 */
// @RestController: handles REST requests and returns JSON.
@RestController
// @RequestMapping: all URLs here start with /api/income.
@RequestMapping("/api/income")
public class IncomeController {

    private final IncomeService incomeService;

    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    // GET /api/income/farmer/1
    //     /api/income/farmer/1?cropId=1&from=2026-10-01&to=2026-10-31&search=rice
    // The farmer id is part of the path. The filters are OPTIONAL query parameters.
    @GetMapping("/farmer/{farmerId}")
    public List<IncomeResponse> getIncomeByFarmer(
            @PathVariable Long farmerId,
            @RequestParam(required = false) Long cropId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String search) {
        return incomeService.getIncomeByFarmer(farmerId, cropId, from, to, search);
    }

    // GET /api/income/5?farmerId=1 -> income 5, only if it belongs to farmer 1
    @GetMapping("/{id}")
    public IncomeResponse getIncome(@PathVariable Long id, @RequestParam Long farmerId) {
        return incomeService.getIncome(id, farmerId);
    }

    // POST /api/income -> create income (JSON body, validated by @Valid)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IncomeResponse createIncome(@Valid @RequestBody IncomeRequest request) {
        // The farmerId in the body must be the logged-in farmer's own id.
        CurrentFarmer.requireSame(request.getFarmerId());
        return incomeService.createIncome(request);
    }

    // PUT /api/income/5 -> update income 5 (farmerId is in the body)
    @PutMapping("/{id}")
    public IncomeResponse updateIncome(@PathVariable Long id, @Valid @RequestBody IncomeRequest request) {
        CurrentFarmer.requireSame(request.getFarmerId());
        return incomeService.updateIncome(id, request);
    }

    // DELETE /api/income/5?farmerId=1 -> delete income 5, only if it belongs to farmer 1
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteIncome(@PathVariable Long id, @RequestParam Long farmerId) {
        incomeService.deleteIncome(id, farmerId);
    }
}