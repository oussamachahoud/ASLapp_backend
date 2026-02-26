package com.example.aslapp_backend.DTOs.responseDTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Schema(description = "Product category")
@Data
@AllArgsConstructor
@Builder
public class CategoryDTO {
    @Schema(description = "Category ID", example = "1")
    Long id;
    @Schema(description = "Category name", example = "Electronics")
    String name;
}
