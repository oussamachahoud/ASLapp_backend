package com.example.aslapp_backend.DTOs.requestDTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Partial update request for user profile")
public class UpdateUserDTO {

    @Schema(description = "New username (3-30 chars)", example = "new_username")
    @Size(min = 3, max = 30)
    private String username;

    @Schema(description = "New email address", example = "newemail@example.com")
    @Pattern(regexp = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$", message = "Invalid email format")
    private String email;

    @Schema(description = "New age", example = "30")
    private Integer age;

    public UpdateUserDTO() {
    }

    public UpdateUserDTO(String username, String email, Integer age) {
        this.username = username;
        this.email = email;
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
