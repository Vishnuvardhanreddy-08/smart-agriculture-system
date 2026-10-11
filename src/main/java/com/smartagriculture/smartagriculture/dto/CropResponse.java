package com.smartagriculture.smartagriculture.dto;

import java.math.BigDecimal;

/**
 * CropResponse is the JSON we SEND BACK to the client.
 * It shows farmerId only, so we never send extra farmer details.
 */
public class CropResponse {

    private Long id;
    private String name;
    private String season;
    private BigDecimal area;
    private Long farmerId;

    // Constructor: the Service uses it to build a response in one line.
    public CropResponse(Long id, String name, String season, BigDecimal area, Long farmerId) {
        this.id = id;
        this.name = name;
        this.season = season;
        this.area = area;
        this.farmerId = farmerId;
    }

    // Only getters are needed: Spring reads them to create the JSON.
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSeason() {
        return season;
    }

    public BigDecimal getArea() {
        return area;
    }

    public Long getFarmerId() {
        return farmerId;
    }
}