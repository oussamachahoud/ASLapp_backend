package com.example.aslapp_backend.models.Enum;

import com.example.aslapp_backend.Exeption.BusinessException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

public enum ERole {
    ROLE_USER,
    ROLE_SELLER,
    ROLE_ADMIN;

    public static ERole findByNumber(ERole eRole) {
        return Arrays.stream(values()).filter(Role -> Role.toString().equalsIgnoreCase(eRole.toString()))
                .findFirst().orElseThrow(()->new BusinessException(HttpStatus.NOT_FOUND, "Role not found"));
    }
}
