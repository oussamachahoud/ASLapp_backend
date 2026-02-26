package com.example.aslapp_backend.DTOs.requestDTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Request body for adding a new address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDTO {

    @Schema(description = "Street address", example = "123 Rue Didouche Mourad")
    @NotBlank(message = "Street is required")
    private String street;

    @Schema(description = "Wilaya (Province)", example = "Alger")
    @NotBlank(message = "Wilaya (Province) is required")
    @Size(max = 100, message = "Wilaya must not exceed 100 characters")
    private String wilaya;

    @Schema(description = "Commune", example = "Bab El Oued")
    @NotBlank(message = "Commune is required")
    @Size(max = 100, message = "Commune must not exceed 100 characters")
    private String commune;

    @Schema(description = "Postal code", example = "16000")
    @NotBlank(message = "Postal code is required")
    @Size(max = 10, message = "Postal code must not exceed 10 characters")
    private String codePostal;
}
