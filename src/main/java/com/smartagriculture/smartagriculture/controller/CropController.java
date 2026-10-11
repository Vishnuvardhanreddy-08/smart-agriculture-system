package com.smartagriculture.smartagriculture.controller;

import com.smartagriculture.smartagriculture.dto.CropRequest;
import com.smartagriculture.smartagriculture.dto.CropResponse;
import com.smartagriculture.smartagriculture.security.CurrentFarmer;
import com.smartagriculture.smartagriculture.service.CropService;
import jakarta.validation.Valid;
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

import java.util.List;

/**
 * CropController handles HTTP requests for crops.
 * A farmerId in the URL is checked automatically by FarmerAccessInterceptor.
 * A farmerId inside the JSON body is checked here with CurrentFarmer.requireSame.
 */
// @RestController: handles REST requests and returns JSON.
@RestController
// @RequestMapping: all URLs here start with /api/crops.
@RequestMapping("/api/crops")
public class CropController {

    private final CropService cropService;

    public CropController(CropService cropService) {
        this.cropService = cropService;
    }

    // GET /api/crops/farmer/1 -> all crops of farmer 1 (the interceptor checks that 1 is YOU)
    @GetMapping("/farmer/{farmerId}")
    public List<CropResponse> getCropsByFarmer(@PathVariable Long farmerId) {
        return cropService.getCropsByFarmer(farmerId);
    }

    // GET /api/crops/5?farmerId=1 -> crop 5, only if it belongs to farmer 1
    @GetMapping("/{id}")
    public CropResponse getCrop(@PathVariable Long id, @RequestParam Long farmerId) {
        return cropService.getCrop(id, farmerId);
    }

    // POST /api/crops -> create a crop
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CropResponse createCrop(@Valid @RequestBody CropRequest request) {
        // The farmerId in the body must be the logged-in farmer's own id.
        CurrentFarmer.requireSame(request.getFarmerId());
        return cropService.createCrop(request);
    }

    // PUT /api/crops/5 -> update crop 5 (farmerId is inside the JSON body)
    @PutMapping("/{id}")
    public CropResponse updateCrop(@PathVariable Long id, @Valid @RequestBody CropRequest request) {
        CurrentFarmer.requireSame(request.getFarmerId());
        return cropService.updateCrop(id, request);
    }

    // DELETE /api/crops/5?farmerId=1 -> delete crop 5, only if it belongs to farmer 1
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCrop(@PathVariable Long id, @RequestParam Long farmerId) {
        cropService.deleteCrop(id, farmerId);
    }
}