package com.smartagriculture.smartagriculture.service;

import com.smartagriculture.smartagriculture.exception.ResourceNotFoundException;
import com.smartagriculture.smartagriculture.model.Farmer;
import com.smartagriculture.smartagriculture.repository.FarmerRepository;
import org.springframework.stereotype.Service;

/**
 * FarmerService holds the business logic for looking up farmers.
 * Creating farmers is done by AuthService (register), so a farmer can never be created
 * without a password.
 */
// @Service tells Spring this class contains business logic.
@Service
public class FarmerService {

    private final FarmerRepository farmerRepository;

    // Constructor injection: Spring automatically gives us the repository object.
    public FarmerService(FarmerRepository farmerRepository) {
        this.farmerRepository = farmerRepository;
    }

    // Get one farmer. If the id does not exist, throw 404.
    // (Used by the Crop, Income, Expense and Financial services.)
    public Farmer getFarmerById(Long id) {
        return farmerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer.notFound",id));
    }
}