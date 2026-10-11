package com.smartagriculture.smartagriculture.service;

import com.smartagriculture.smartagriculture.dto.ExpenseRequest;
import com.smartagriculture.smartagriculture.dto.ExpenseResponse;
import com.smartagriculture.smartagriculture.exception.ResourceNotFoundException;
import com.smartagriculture.smartagriculture.model.Crop;
import com.smartagriculture.smartagriculture.model.Expense;
import com.smartagriculture.smartagriculture.model.Farmer;
import com.smartagriculture.smartagriculture.repository.CropRepository;
import com.smartagriculture.smartagriculture.repository.ExpenseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.smartagriculture.smartagriculture.exception.ApiException;

import java.time.LocalDate;
import java.util.List;

/**
 * ExpenseService holds the business logic for expenses.
 * Every method works for ONE farmer, so expenses never mix between farmers.
 */
// @Service tells Spring this class contains business logic.
@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CropRepository cropRepository;
    private final FarmerService farmerService;

    // Constructor injection: Spring provides these objects.
    public ExpenseService(ExpenseRepository expenseRepository,
                          CropRepository cropRepository,
                          FarmerService farmerService) {
        this.expenseRepository = expenseRepository;
        this.cropRepository = cropRepository;
        this.farmerService = farmerService;
    }

    // Get the expenses of ONE farmer, newest first, with optional filters.
    // Any filter that is null is ignored.
    //   cropId : only expenses linked to this crop
    //   from   : only expenses on or after this date
    //   to     : only expenses on or before this date
    //   search : text that must appear in the category or description (not case-sensitive)
    public List<ExpenseResponse> getExpensesByFarmer(Long farmerId, Long cropId,
                                                     LocalDate from, LocalDate to, String search) {
        farmerService.getFarmerById(farmerId); // 404 if the farmer does not exist

        if (from != null && to != null && from.isAfter(to)) {
            // Returns HTTP 400 with the message for the key "date.range".
            throw new ApiException(HttpStatus.BAD_REQUEST, "date.range");
        }

        // Clean the search text once: ignore blank text, and use lower case for comparing.
        final String keyword = (search == null || search.isBlank()) ? null : search.trim().toLowerCase();

        // Start from THIS farmer's expenses only, then remove records that do not match.
        return expenseRepository.findByFarmerIdOrderByDateDesc(farmerId)
                .stream()
                .filter(e -> cropId == null
                        || (e.getCrop() != null && cropId.equals(e.getCrop().getId())))
                .filter(e -> from == null || !e.getDate().isBefore(from))
                .filter(e -> to == null || !e.getDate().isAfter(to))
                .filter(e -> keyword == null
                        || contains(e.getCategory(), keyword)
                        || contains(e.getDescription(), keyword))
                .map(this::toResponse)
                .toList();
    }

    // Get one expense, only if it belongs to this farmer.
    public ExpenseResponse getExpense(Long id, Long farmerId) {
        return toResponse(findOwnedExpense(id, farmerId));
    }

    // Create a new expense.
    public ExpenseResponse createExpense(ExpenseRequest request) {
        Farmer farmer = farmerService.getFarmerById(request.getFarmerId());
        // The crop (if given) must belong to the SAME farmer.
        Crop crop = resolveCrop(request.getCropId(), request.getFarmerId());

        Expense expense = new Expense();
        expense.setFarmer(farmer);
        applyRequest(expense, request, crop);

        return toResponse(expenseRepository.save(expense));
    }

    // Update an expense, only if it belongs to the farmer in the request.
    public ExpenseResponse updateExpense(Long id, ExpenseRequest request) {
        Expense expense = findOwnedExpense(id, request.getFarmerId());
        Crop crop = resolveCrop(request.getCropId(), request.getFarmerId());

        // The owner (farmer) is NEVER changed during an update.
        applyRequest(expense, request, crop);

        return toResponse(expenseRepository.save(expense));
    }

    // Delete an expense, only if it belongs to this farmer.
    public void deleteExpense(Long id, Long farmerId) {
        expenseRepository.delete(findOwnedExpense(id, farmerId));
    }

    // Helper: true if the text contains the keyword (not case-sensitive). Null text never matches.
    private boolean contains(String text, String keyword) {
        return text != null && text.toLowerCase().contains(keyword);
    }

    // Helper: copy the editable fields from the request into the entity.
    private void applyRequest(Expense expense, ExpenseRequest request, Crop crop) {
        expense.setCategory(request.getCategory());
        expense.setAmount(request.getAmount());
        expense.setDate(request.getDate());
        expense.setDescription(request.getDescription());
        expense.setCrop(crop);
    }

    // Helper: find the crop only if it belongs to this farmer.
    // If no cropId was given, the expense simply has no crop (null).
    private Crop resolveCrop(Long cropId, Long farmerId) {
        if (cropId == null) {
            return null;
        }
        return cropRepository.findByIdAndFarmerId(cropId, farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("crop.notFound", cropId, farmerId));
    }

    // Helper: find an expense that belongs to the farmer, otherwise 404.
    private Expense findOwnedExpense(Long id, Long farmerId) {
        return expenseRepository.findByIdAndFarmerId(id, farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("expense.notFound", id, farmerId));
    }

    // Helper: convert an Expense entity into an ExpenseResponse DTO.
    private ExpenseResponse toResponse(Expense expense) {
        Crop crop = expense.getCrop();
        return new ExpenseResponse(
                expense.getId(),
                expense.getCategory(),
                expense.getAmount(),
                expense.getDate(),
                expense.getDescription(),
                expense.getFarmer().getId(),
                crop == null ? null : crop.getId(),
                crop == null ? null : crop.getName());
    }
}