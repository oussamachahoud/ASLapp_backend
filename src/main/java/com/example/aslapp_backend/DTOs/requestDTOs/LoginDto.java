package com.example.aslapp_backend.DTOs.requestDTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Login credentials")
@Getter@Setter
@AllArgsConstructor
public class LoginDto {

    @Schema(description = "User email", example = "are123ach@gmail.com")
    @Email
    @NotBlank
    private String email;

    @Schema(description = "User password", example = "renad16637")
    @NotBlank
   // @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$", message = "Password not strong enough" )
    private String password;
}
