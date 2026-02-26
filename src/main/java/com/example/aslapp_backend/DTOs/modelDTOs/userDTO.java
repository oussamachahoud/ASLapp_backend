package com.example.aslapp_backend.DTOs.modelDTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Schema(description = "User summary")
@Getter
@Setter
@Builder
public class userDTO {
    @Schema(description = "User ID", example = "1")
    private Long id;
    @Schema(description = "Username", example = "john_doe")
    private String username;
    @Schema(description = "Email", example = "john@example.com")
    private String email;
    @Schema(description = "Age", example = "25")
    private int age;
    @Schema(description = "Profile image URL")
    private String imageURL;
    @Schema(description = "User roles", example = "[\"ROLE_USER\"]")
    private Set<String> role;
}