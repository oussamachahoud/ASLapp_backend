package com.example.aslapp_backend.DTOs.responseDTOs;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Address details")
public record AddressResponseDTO(
        @Schema(description = "Address ID", example = "1") Long id,
        @Schema(description = "Street", example = "123 Rue Didouche Mourad") String street,
        @Schema(description = "Wilaya", example = "Alger") String wilaya,
        @Schema(description = "Commune", example = "Bab El Oued") String commune,
        @Schema(description = "Postal code", example = "16000") String codePostal
) {}
