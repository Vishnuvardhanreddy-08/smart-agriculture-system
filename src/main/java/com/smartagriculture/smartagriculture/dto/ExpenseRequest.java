package com.smartagriculture.smartagriculture.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * ExpenseRequest is the JSON the client SENDS to create or update an expense.
 * The messages are KEYS in curly braces, looked up in the language of the request.
 */
public class ExpenseRequest {

    @NotBlank(message = "{validation.category.required}")
    private String category;

    // Amount must be present, greater than 0, and fit our database column.
    // @Digits allows at most 10 digits before the decimal point and 2 after (matches numeric(12,2)).
    @NotNull(message = "{validation.amount.required}")
    @DecimalMin(value = "0.01", message = "{validation.amount.min}")
    @Digits(integer = 10, fraction = 2, message = "{validation.amount.digits}")
    private BigDecimal amount;

    // Date format in JSON: "2026-10-06". A wrong date gives a 400 error automatically.
    @NotNull(message = "{validation.date.required}")
    private LocalDate date;

    private String description;

    @NotNull(message = "{validation.farmerId.required}")
    private Long farmerId;

    // Optional: leave it out (or null) for an expense that has no crop.
    private Long cropId;

    // Getters and setters
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(Long farmerId) {
        this.farmerId = farmerId;
    }

    public Long getCropId() {
        return cropId;
    }

    public void setCropId(Long cropId) {
        this.cropId = cropId;
    }
}