package com.smartagriculture.smartagriculture.service;

import com.smartagriculture.smartagriculture.dto.IncomeRequest;
import com.smartagriculture.smartagriculture.dto.IncomeResponse;
import com.smartagriculture.smartagriculture.exception.ResourceNotFoundException;
import com.smartagriculture.smartagriculture.model.Crop;
import com.smartagriculture.smartagriculture.model.Farmer;
import com.smartagriculture.smartagriculture.model.Income;
import com.smartagriculture.smartagriculture.repository.CropRepository;
import com.smartagriculture.smartagriculture.repository.IncomeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.smartagriculture.smartagriculture.exception.ApiException;

import java.time.LocalDate;
import java.util.List;

/**
 * IncomeService holds the business logic for income.
 * Every method works for ONE farmer, so income never mixes between farmers.
 */
// @Service tells Spring this class contains business logic.
@Service
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final CropRepository cropRepository;
    private final FarmerService farmerService;

    // Constructor injection: Spring provides these objects.
    public IncomeService(IncomeRepository incomeRepository,
                         CropRepository cropRepository,
                         FarmerService farmerService) {
        this.incomeRepository = incomeRepository;
        this.cropRepository = cropRepository;
        this.farmerService = farmerService;
    }

    // Get the income of ONE farmer, newest first, with optional filters.
    // Any filter that is null is ignored.
    //   cropId : only income linked to this crop
    //   from   : only income on or after this date
    //   to     : only income on or before this date
    //   search : text that must appear in the source or description (not case-sensitive)
    public List<IncomeResponse> getIncomeByFarmer(Long farmerId, Long cropId,
                                                  LocalDate from, LocalDate to, String search) {
        farmerService.getFarmerById(farmerId); // 404 if the farmer does not exist

        if (from != null && to != null && from.isAfter(to)) {
            // Returns HTTP 400 with this message.
            // Returns HTTP 400 with the message for the key "date.range".
            throw new ApiException(HttpStatus.BAD_REQUEST, "date.range");

        }

        // Clean the search text once: ignore blank text, and use lower case for comparing.
        final String keyword = (search == null || search.isBlank()) ? null : search.trim().toLowerCase();

        // Start from THIS farmer's income only, then remove records that do not match.
        return incomeRepository.findByFarmerIdOrderByDateDesc(farmerId)
                .stream()
                .filter(i -> cropId == null
                        || (i.getCrop() != null && cropId.equals(i.getCrop().getId())))
                .filter(i -> from == null || !i.getDate().isBefore(from))
                .filter(i -> to == null || !i.getDate().isAfter(to))
                .filter(i -> keyword == null
                        || contains(i.getSource(), keyword)
                        || contains(i.getDescription(), keyword))
                .map(this::toResponse)
                .toList();
    }

    // Get one income record, only if it belongs to this farmer.
    public IncomeResponse getIncome(Long id, Long farmerId) {
        return toResponse(findOwnedIncome(id, farmerId));
    }

    // Create a new income record.
    public IncomeResponse createIncome(IncomeRequest request) {
        Farmer farmer = farmerService.getFarmerById(request.getFarmerId());
        // The crop (if given) must belong to the SAME farmer.
        Crop crop = resolveCrop(request.getCropId(), request.getFarmerId());

        Income income = new Income();
        income.setFarmer(farmer);
        applyRequest(income, request, crop);

        return toResponse(incomeRepository.save(income));
    }

    // Update an income record, only if it belongs to the farmer in the request.
    public IncomeResponse updateIncome(Long id, IncomeRequest request) {
        Income income = findOwnedIncome(id, request.getFarmerId());
        Crop crop = resolveCrop(request.getCropId(), request.getFarmerId());

        // The owner (farmer) is NEVER changed during an update.
        applyRequest(income, request, crop);

        return toResponse(incomeRepository.save(income));
    }

    // Delete an income record, only if it belongs to this farmer.
    public void deleteIncome(Long id, Long farmerId) {
        incomeRepository.delete(findOwnedIncome(id, farmerId));
    }

    // Helper: true if the text contains the keyword (not case-sensitive). Null text never matches.
    private boolean contains(String text, String keyword) {
        return text != null && text.toLowerCase().contains(keyword);
    }

    // Helper: copy the editable fields from the request into the entity.
    private void applyRequest(Income income, IncomeRequest request, Crop crop) {
        income.setSource(request.getSource());
        income.setAmount(request.getAmount());
        income.setDate(request.getDate());
        income.setDescription(request.getDescription());
        income.setCrop(crop);
    }

    // Helper: find the crop only if it belongs to this farmer.
    // If no cropId was given, the income simply has no crop (null).
    private Crop resolveCrop(Long cropId, Long farmerId) {
        if (cropId == null) {
            return null;
        }
        return cropRepository.findByIdAndFarmerId(cropId, farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("crop.notFound" ,cropId,farmerId));
    }

    // Helper: find an income that belongs to the farmer, otherwise 404.
    private Income findOwnedIncome(Long id, Long farmerId) {
        return incomeRepository.findByIdAndFarmerId(id, farmerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "income.notFound ",id,farmerId));
    }

    // Helper: convert an Income entity into an IncomeResponse DTO.
    private IncomeResponse toResponse(Income income) {
        Crop crop = income.getCrop();
        return new IncomeResponse(
                income.getId(),
                income.getSource(),
                income.getAmount(),
                income.getDate(),
                income.getDescription(),
                income.getFarmer().getId(),
                crop == null ? null : crop.getId(),
                crop == null ? null : crop.getName());
    }
}