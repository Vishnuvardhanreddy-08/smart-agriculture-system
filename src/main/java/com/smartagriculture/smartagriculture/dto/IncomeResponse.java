package com.smartagriculture.smartagriculture.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * IncomeResponse is the JSON we SEND BACK.
 * We include cropName so the screen can show "Rice" without another request.
 */
public class IncomeResponse {

    private Long id;
    private String source;
    private BigDecimal amount;
    private LocalDate date;
    private String description;
    private Long farmerId;
    private Long cropId;
    private String cropName;

    // Constructor used by the Service to build a response.
    public IncomeResponse(Long id, String source, BigDecimal amount, LocalDate date,
                          String description, Long farmerId, Long cropId, String cropName) {
        this.id = id;
        this.source = source;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.farmerId = farmerId;
        this.cropId = cropId;
        this.cropName = cropName;
    }

    // Only getters are needed to create the JSON.
    public Long getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public Long getFarmerId() {
        return farmerId;
    }

    public Long getCropId() {
        return cropId;
    }

    public String getCropName() {
        return cropName;
    }
}