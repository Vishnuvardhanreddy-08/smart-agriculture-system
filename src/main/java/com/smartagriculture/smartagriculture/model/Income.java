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
import java.time.LocalDate;

/**
 * Income represents one money-earning record (for example "Rice Sale, 35000").
 * Each object = one row in the "incomes" table.
 */
// @Entity tells JPA that this class represents a database table.
@Entity
@Table(name = "incomes")
public class Income {

    // Primary key, generated automatically by the database.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // What the money came from, for example "Rice Sale".
    @Column(nullable = false)
    private String source;

    // Amount in rupees. BigDecimal is exact (safe for money).
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    // The date money was received. The column is named income_date to avoid
    // using the word "date", which is also a database keyword.
    @Column(name = "income_date", nullable = false)
    private LocalDate date;

    // Optional note. length = 500 allows a longer text.
    @Column(length = 500)
    private String description;

    // Many income records can belong to ONE farmer. Required.
    // LAZY = load the farmer only when needed.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    // @JoinColumn creates the foreign-key column farmer_id.
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    // Many income records can belong to ONE crop. Optional (can be null).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id")
    private Crop crop;

    // JPA needs a no-argument constructor.
    public Income() {
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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

    public Farmer getFarmer() {
        return farmer;
    }

    public void setFarmer(Farmer farmer) {
        this.farmer = farmer;
    }

    public Crop getCrop() {
        return crop;
    }

    public void setCrop(Crop crop) {
        this.crop = crop;
    }
}