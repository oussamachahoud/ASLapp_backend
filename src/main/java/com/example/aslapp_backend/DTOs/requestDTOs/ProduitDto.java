package com.example.aslapp_backend.DTOs.requestDTOs;

import com.example.aslapp_backend.DTOs.responseDTOs.CategoryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Schema(description = "Request body for creating or updating a product")
public class ProduitDto {

    @Schema(description = "Product name", example = "ASL T-Shirt")
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be at most 255 characters")
    private String name;

    @Schema(description = "Product price", example = "29.99")
    @PositiveOrZero(message = "Price must be zero or positive")
    private double price;

    @Schema(description = "Product description", example = "High quality cotton T-Shirt")
    @Size(max = 1000, message = "Description must be at most 1000 characters")
    private String description;

    @Schema(description = "Product category")
    @NotNull(message = "Category is required")
    private CategoryDTO category;

    @Schema(description = "Available stock quantity", example = "100")
    @PositiveOrZero(message = "Stock must be zero or positive")
    private int stock;

    // No-args constructor
    public ProduitDto() {
    }

    // All-args constructor (including id)
    public ProduitDto(String name, double price, String description, CategoryDTO category, int stock) {

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

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}