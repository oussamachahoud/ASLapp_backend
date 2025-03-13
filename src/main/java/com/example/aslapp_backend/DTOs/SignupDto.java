package com.example.aslapp_backend.DTOs;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class SignupDto {

    @NotBlank
    @Size(min = 2, max = 50)
    private String username;


    @NotBlank
    @Size(max = 120)
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$",
    message="password not strong enough")
    private String password;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 3)
    private int age;

    @NotBlank
    @Size(max = 300)
    private String reason;


    //private Set<String> roles = new HashSet<>();
}
