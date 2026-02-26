package com.example.aslapp_backend.DTOs.requestDTOs;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "User registration request")
@Getter
@Setter
@AllArgsConstructor
public class SignupDto {

    @Schema(description = "Username (2-50 chars)", example = "john_doe")
    @NotBlank
    @Size(min = 2, max = 50)
    private String username;

    @Schema(description = "Password (max 120 chars)", example = "Str0ng!Pass")
    @NotBlank
    @Size(max = 120)
   // @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$",
   // message="password not strong enough")
    private String password;

    @Schema(description = "Email address", example = "mohamd@example.com")
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @Schema(description = "User age", example = "25")
    private int age;

    @Schema(description = "Reason for signing up", example = "I want to buy ASL products")
    @NotBlank
    @Size(max = 300)
    private String reason;


    //private Set<String> roles = new HashSet<>();
}
