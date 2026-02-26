package com.example.aslapp_backend.DTOs.requestDTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Category creation request")
@Data
public class CategoryRequestDTO {

    @Schema(description = "Category name", example = "Electronics", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

}
