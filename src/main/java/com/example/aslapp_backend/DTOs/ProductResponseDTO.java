package com.example.aslapp_backend.DTOs;

import com.example.aslapp_backend.models.Category;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class ProductResponseDTO {
    private long id;
    private String name;
    private double price;
    private String description;
    private String imageURL;
    private Category category;
    @PositiveOrZero
    @NotNull
    private int stock;
}
