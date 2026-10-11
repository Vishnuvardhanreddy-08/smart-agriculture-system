package com.smartagriculture.smartagriculture.dto;

import java.math.BigDecimal;

/**
 * TotalAmountResponse is used for "total income" and "total expenses".
 * It tells which farmer the total belongs to, and the total amount.
 */
public class TotalAmountResponse {

    private Long farmerId;
    private BigDecimal totalAmount;

    public TotalAmountResponse(Long farmerId, BigDecimal totalAmount) {
        this.farmerId = farmerId;
        this.totalAmount = totalAmount;
    }

    // Only getters are needed to create the JSON.
    public Long getFarmerId() {
        return farmerId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}