package com.smartagriculture.smartagriculture.repository;

import com.smartagriculture.smartagriculture.dto.CropTotal;
import com.smartagriculture.smartagriculture.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * IncomeRepository talks to the "incomes" table.
 */
// @Repository tells Spring this interface communicates with the database.
@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {

    // SELECT * FROM incomes WHERE farmer_id = ? ORDER BY income_date DESC
    List<Income> findByFarmerIdOrderByDateDesc(Long farmerId);

    // Returns the income only if it belongs to that farmer.
    Optional<Income> findByIdAndFarmerId(Long id, Long farmerId);

    // Adds up ONE farmer's income using SUM. Returns null if there are no rows.
    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.farmer.id = :farmerId")
    BigDecimal sumAmountByFarmerId(@Param("farmerId") Long farmerId);

    // Income total for each crop of ONE farmer (null cropId = income with no crop).
    @Query("SELECT c.id AS cropId, SUM(i.amount) AS total "
            + "FROM Income i LEFT JOIN i.crop c "
            + "WHERE i.farmer.id = :farmerId "
            + "GROUP BY c.id")
    List<CropTotal> sumAmountByCrop(@Param("farmerId") Long farmerId);

    // ONE farmer's income between two dates (both dates included).
    List<Income> findByFarmerIdAndDateBetween(Long farmerId, LocalDate start, LocalDate end);

    // NEW: true if at least one income record uses this crop.
    // Spring builds "SELECT ... WHERE crop_id = ?" from the name and returns true or false.
    boolean existsByCropId(Long cropId);
}