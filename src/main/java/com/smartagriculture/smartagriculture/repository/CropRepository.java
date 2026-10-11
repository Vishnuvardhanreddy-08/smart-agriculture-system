package com.smartagriculture.smartagriculture.repository;

import com.smartagriculture.smartagriculture.model.Crop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * CropRepository talks to the "crops" table.
 * We only write method NAMES. Spring reads each name and creates the SQL.
 */
// @Repository tells Spring this interface communicates with the database.
@Repository
public interface CropRepository extends JpaRepository<Crop, Long> {

    // "findBy" + "Farmer" + "Id" -> SELECT * FROM crops WHERE farmer_id = ?
    // Returns ONLY this farmer's crops. This is our data isolation.
    List<Crop> findByFarmerId(Long farmerId);

    // SELECT * FROM crops WHERE id = ? AND farmer_id = ?
    // Returns the crop only if it belongs to that farmer. Otherwise it is empty.
    Optional<Crop> findByIdAndFarmerId(Long id, Long farmerId);
}