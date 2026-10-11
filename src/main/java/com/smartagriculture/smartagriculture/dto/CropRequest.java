package com.smartagriculture.smartagriculture.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * CropRequest is the JSON the client SENDS when creating or updating a crop.
 * It contains only farmerId (a number), not a whole Farmer object.
 * The messages are KEYS in curly braces: they are looked up in messages.properties
 * (English) or messages_te.properties (Telugu), depending on the request's language.
 */
public class CropRequest {

    // @NotBlank: cannot be null, empty or only spaces.
    @NotBlank(message = "{validation.crop.name.required}")
    private String name;

    @NotBlank(message = "{validation.season.required}")
    private String season;

    // @NotNull: a value must be given. @DecimalMin: at least 0.01 (so area > 0).
    // @Digits: at most 8 digits before the decimal point and 2 after (matches numeric(10,2)).
    @NotNull(message = "{validation.area.required}")
    @DecimalMin(value = "0.01", message = "{validation.area.min}")
    @Digits(integer = 8, fraction = 2, message = "{validation.area.digits}")
    private BigDecimal area;

    @NotNull(message = "{validation.farmerId.required}")
    private Long farmerId;

    // Getters and setters (Spring uses them to fill this object from the JSON).
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public Long getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(Long farmerId) {
        this.farmerId = farmerId;
    }
}