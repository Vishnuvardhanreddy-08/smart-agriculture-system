package com.smartagriculture.smartagriculture.service;

import com.smartagriculture.smartagriculture.dto.CropRequest;
import com.smartagriculture.smartagriculture.dto.CropResponse;
import com.smartagriculture.smartagriculture.exception.ConflictException;
import com.smartagriculture.smartagriculture.exception.ResourceNotFoundException;
import com.smartagriculture.smartagriculture.model.Crop;
import com.smartagriculture.smartagriculture.model.Farmer;
import com.smartagriculture.smartagriculture.repository.CropRepository;
import com.smartagriculture.smartagriculture.repository.ExpenseRepository;
import com.smartagriculture.smartagriculture.repository.IncomeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CropService holds the business logic for crops.
 * Every method works for ONE farmer, so data never mixes.
 */
// @Service tells Spring this class contains business logic.
@Service
public class CropService {

    private final CropRepository cropRepository;
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final FarmerService farmerService;

    // Constructor injection: Spring provides all these objects.
    public CropService(CropRepository cropRepository,
                       IncomeRepository incomeRepository,
                       ExpenseRepository expenseRepository,
                       FarmerService farmerService) {
        this.cropRepository = cropRepository;
        this.incomeRepository = incomeRepository;
        this.expenseRepository = expenseRepository;
        this.farmerService = farmerService;
    }

    // Get all crops of ONE farmer.
    public List<CropResponse> getCropsByFarmer(Long farmerId) {
        // Make sure the farmer exists (throws 404 if not).
        farmerService.getFarmerById(farmerId);

        return cropRepository.findByFarmerId(farmerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Get one crop, only if it belongs to this farmer.
    public CropResponse getCrop(Long id, Long farmerId) {
        return toResponse(findOwnedCrop(id, farmerId));
    }

    // Create a crop for the farmer given in the request.
    public CropResponse createCrop(CropRequest request) {
        // Load the real farmer (404 if not found).
        Farmer farmer = farmerService.getFarmerById(request.getFarmerId());

        Crop crop = new Crop();
        crop.setName(request.getName());
        crop.setSeason(request.getSeason());
        crop.setArea(request.getArea());
        crop.setFarmer(farmer);

        return toResponse(cropRepository.save(crop));
    }

    // Update a crop, only if it belongs to the farmer in the request.
    public CropResponse updateCrop(Long id, CropRequest request) {
        Crop crop = findOwnedCrop(id, request.getFarmerId());

        // We change only name, season and area. The owner is NEVER changed.
        crop.setName(request.getName());
        crop.setSeason(request.getSeason());
        crop.setArea(request.getArea());

        return toResponse(cropRepository.save(crop));
    }

    // Delete a crop, only if it belongs to this farmer AND no records use it.
    public void deleteCrop(Long id, Long farmerId) {
        Crop crop = findOwnedCrop(id, farmerId);

        // NEW: refuse politely (409) if income or expense records still point to this crop.
        if (incomeRepository.existsByCropId(id) || expenseRepository.existsByCropId(id)) {
            throw new ConflictException("crop.hasRecords",crop.getName());
        }

        cropRepository.delete(crop);
    }

    // Helper: find a crop that belongs to the farmer, otherwise throw 404.
    // We use 404 (not "forbidden") so nobody can discover other farmers' crop ids.
    private Crop findOwnedCrop(Long id, Long farmerId) {
        return cropRepository.findByIdAndFarmerId(id, farmerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Crop.notFound",id,farmerId));
    }

    // Helper: convert a Crop entity into a CropResponse DTO.
    private CropResponse toResponse(Crop crop) {
        return new CropResponse(
                crop.getId(),
                crop.getName(),
                crop.getSeason(),
                crop.getArea(),
                crop.getFarmer().getId());
    }
}