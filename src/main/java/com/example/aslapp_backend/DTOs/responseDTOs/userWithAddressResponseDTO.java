package com.example.aslapp_backend.DTOs.responseDTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.Set;

@Schema(description = "User profile with addresses")
@Builder
public record userWithAddressResponseDTO(
        @Schema(description = "User ID", example = "1") Long id,
        @Schema(description = "Username", example = "john_doe") String username,
        @Schema(description = "Email", example = "john@example.com") String email,
        @Schema(description = "Age", example = "25") int age,
        @Schema(description = "Profile image URL") String imageURL,
        @Schema(description = "User addresses") Set<AddressResponseDTO> addresses,
        @Schema(description = "User roles", example = "[\"ROLE_USER\"]") Set<String> role

) {}