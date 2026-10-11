package com.smartagriculture.smartagriculture.repository;

import com.smartagriculture.smartagriculture.model.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * FarmerRepository talks to the "farmers" table.
 * We write NO code inside it. Spring creates the implementation automatically.
 */
// @Repository tells Spring this interface communicates with the database.
@Repository
public interface FarmerRepository extends JpaRepository<Farmer, Long> {

    // NEW: true if a farmer with this phone number already exists (used when registering).
    boolean existsByPhone(String phone);

    // NEW: finds the farmer with this phone number (used when logging in).
    Optional<Farmer> findByPhone(String phone);
}