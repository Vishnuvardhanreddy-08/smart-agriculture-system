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
 * Expense represents one spending record (for example "Fertilizer, 5000").
 * Each object = one row in the "expenses" table.
 */
// @Entity tells JPA that this class represents a database table.
@Entity
@Table(name = "expenses")
public class Expense {

    // Primary key, generated automatically by the database.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // What the money was spent on, for example "Fertilizer".
    @Column(nullable = false)
    private String category;

    // Amount in rupees. BigDecimal is exact (safe for money).
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    // The date the money was spent. Column name avoids the keyword "date".
    @Column(name = "expense_date", nullable = false)
    private LocalDate date;

    // Optional note.
    @Column(length = 500)
    private String description;

    // Many expenses can belong to ONE farmer. Required.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    // @JoinColumn creates the foreign-key column farmer_id.
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    // Many expenses can belong to ONE crop. Optional (for example, electricity bills).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id")
    private Crop crop;

    // JPA needs a no-argument constructor.
    public Expense() {
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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