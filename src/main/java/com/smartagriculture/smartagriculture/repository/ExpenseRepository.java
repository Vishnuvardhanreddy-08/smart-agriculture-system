package com.smartagriculture.smartagriculture.repository;

import com.smartagriculture.smartagriculture.dto.CropTotal;
import com.smartagriculture.smartagriculture.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * ExpenseRepository talks to the "expenses" table.
 */
// @Repository tells Spring this interface communicates with the database.
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // SELECT * FROM expenses WHERE farmer_id = ? ORDER BY expense_date DESC
    List<Expense> findByFarmerIdOrderByDateDesc(Long farmerId);

    // Returns the expense only if it belongs to that farmer.
    Optional<Expense> findByIdAndFarmerId(Long id, Long farmerId);

    // Adds up ONE farmer's expenses using SUM. Returns null if there are no rows.
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.farmer.id = :farmerId")
    BigDecimal sumAmountByFarmerId(@Param("farmerId") Long farmerId);

    // Expense total for each crop of ONE farmer (null cropId = expenses with no crop).
    @Query("SELECT c.id AS cropId, SUM(e.amount) AS total "
            + "FROM Expense e LEFT JOIN e.crop c "
            + "WHERE e.farmer.id = :farmerId "
            + "GROUP BY c.id")
    List<CropTotal> sumAmountByCrop(@Param("farmerId") Long farmerId);

    // ONE farmer's expenses between two dates (both dates included).
    List<Expense> findByFarmerIdAndDateBetween(Long farmerId, LocalDate start, LocalDate end);

    // NEW: true if at least one expense record uses this crop.
    boolean existsByCropId(Long cropId);
}