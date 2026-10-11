package com.smartagriculture.smartagriculture.dto;

import java.math.BigDecimal;

/**
 * CropTotal is a "projection": a small read-only view of query results.
 * Spring fills it from a query that returns two columns named cropId and total.
 * cropId is null for records that have no crop.
 */
public interface CropTotal {

    // Matches "AS cropId" in the query.
    Long getCropId();

    // Matches "AS total" in the query.
    BigDecimal getTotal();
}