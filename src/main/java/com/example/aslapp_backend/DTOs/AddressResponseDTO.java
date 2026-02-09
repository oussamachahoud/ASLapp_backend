package com.example.aslapp_backend.DTOs;

public record AddressResponseDTO(
        Long id,
        String street,
        String wilaya,
        String commune,
        String codePostal
) {}
