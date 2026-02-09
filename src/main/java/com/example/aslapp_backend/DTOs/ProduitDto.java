package com.example.aslapp_backend.DTOs;

import com.example.aslapp_backend.models.Category;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class ProduitDto {


    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be at most 255 characters")
    private String name;

    @PositiveOrZero(message = "Price must be zero or positive")
    private double price;

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    private String description;

    @NotNull(message = "Category is required")
    @Valid
    private Category category;

    @PositiveOrZero(message = "Stock must be zero or positive")
    private int stock;

    // No-args constructor
    public ProduitDto() {
    }

    // All-args constructor (including id)
    public ProduitDto(String name, double price, String description, Category category, int stock) {

        this.name = name;
        this.price = price;
        this.description = description;
        this.category = category;
        this.stock = stock;
    }


    // Getters and setters


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}