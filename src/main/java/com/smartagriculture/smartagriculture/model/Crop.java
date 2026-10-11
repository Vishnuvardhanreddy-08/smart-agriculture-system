package com.smartagriculture.smartagriculture.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Crop represents one crop (Rice, Maize...) that belongs to ONE farmer.
 * Each object = one row in the "crops" table.
 */
// @Entity tells JPA that this class represents a database table.
@Entity
// @Table sets the table name.
@Table(name = "crops")
public class Crop {

    // @Id = primary key, @GeneratedValue = database creates the id automatically.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column(nullable = false) means this column cannot be empty in the database.
    @Column(nullable = false)
    private String name;

    // Season example: Kharif, Rabi, Zaid
    @Column(nullable = false)
    private String season;

    // Area in acres. BigDecimal is exact for decimal numbers (better than double).
    // precision = 10 total digits, scale = 2 digits after the decimal point.
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal area;

    // @ManyToOne means many crops can belong to one farmer.
    // LAZY means: don't load the farmer from the database until we really need it.
    // optional = false means a crop MUST have a farmer.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    // @JoinColumn creates the foreign-key column "farmer_id" in the crops table.
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    // JPA needs a no-argument constructor.
    public Crop() {
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Farmer getFarmer() {
        return farmer;
    }

    public void setFarmer(Farmer farmer) {
        this.farmer = farmer;
    }
}