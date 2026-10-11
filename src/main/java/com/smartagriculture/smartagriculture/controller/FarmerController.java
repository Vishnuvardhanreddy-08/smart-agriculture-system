package com.smartagriculture.smartagriculture.controller;

import com.smartagriculture.smartagriculture.dto.FarmerResponse;
import com.smartagriculture.smartagriculture.security.CurrentFarmer;
import com.smartagriculture.smartagriculture.service.FarmerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * FarmerController lets a logged-in farmer see THEIR OWN profile.
 * There is no "list all farmers" any more: one farmer must never see the others.
 */
// @RestController: handles REST requests and returns JSON.
@RestController
// @RequestMapping: all URLs here start with /api/farmers.
@RequestMapping("/api/farmers")
public class FarmerController {

    private final FarmerService farmerService;

    public FarmerController(FarmerService farmerService) {
        this.farmerService = farmerService;
    }

    // GET /api/farmers/me -> the profile of the farmer who is logged in.
    // Which farmer? CurrentFarmer.id() reads it from the TOKEN. The client sends no id at all.
    @GetMapping("/me")
    public FarmerResponse getMyProfile() {
        return FarmerResponse.from(farmerService.getFarmerById(CurrentFarmer.id()));
    }
}