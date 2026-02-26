package com.example.aslapp_backend.DTOs.responseDTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Product details response")
@Builder
@Getter
@Setter
@AllArgsConstructor
public class ProductResponseDTO {
    @Schema(description = "Product ID", example = "1")
    private long id;
    @Schema(description = "Product name", example = "ASL T-Shirt")
    private String name;
    @Schema(description = "Product price", example = "29.99")
    private double price;
    @Schema(description = "Product description", example = "High quality cotton T-Shirt")
    private String description;
    @Schema(description = "Image URL", example = "https://storage.example.com/img.jpg")
    private String imageURL;
    @Schema(description = "Product category")
    private CategoryDTO category;
    @Schema(description = "Available stock", example = "100")
    @PositiveOrZero
    @NotNull
    private int stock;
}