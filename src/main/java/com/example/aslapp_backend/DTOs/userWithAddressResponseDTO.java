package com.example.aslapp_backend.DTOs;

import lombok.Builder;

import java.util.List;

@Builder
public record userWithAddressResponseDTO(
        Long id,
        String username,
        String email,
        int age,
        String imageURL,
        List<AddressResponseDTO> addresses

) {}