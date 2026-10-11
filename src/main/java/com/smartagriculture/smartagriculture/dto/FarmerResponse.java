package com.smartagriculture.smartagriculture.dto;

import com.smartagriculture.smartagriculture.model.Farmer;

/**
 * FarmerResponse is the farmer information we are allowed to SEND OUT.
 * There is NO password and NO password hash here, on purpose.
 */
public class FarmerResponse {

    private Long id;
    private String name;
    private String phone;
    private String email;

    public FarmerResponse(Long id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    // Helper: builds a FarmerResponse from a Farmer entity, copying only the safe fields.
    public static FarmerResponse from(Farmer farmer) {
        return new FarmerResponse(farmer.getId(), farmer.getName(), farmer.getPhone(), farmer.getEmail());
    }

    // Only getters are needed to create the JSON.
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }
}