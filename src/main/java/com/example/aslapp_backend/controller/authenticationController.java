package com.example.aslapp_backend.controller;

import com.example.aslapp_backend.DTOs.LoginDto;
import com.example.aslapp_backend.DTOs.SignupDto;
import com.example.aslapp_backend.models.user;
import com.example.aslapp_backend.sevices.AuthenticationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
public class authenticationController {

    private final AuthenticationService authenticationService;
    public authenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @SneakyThrows
    @PostMapping("/singnup")
    public ResponseEntity<user> singnup(@Valid @RequestBody SignupDto signupDto) {
    user user = authenticationService.Signup(signupDto);
    return ResponseEntity.ok(user);
    }

    @SneakyThrows
    @PostMapping("/login")
    public ResponseEntity<user> login(@Valid @RequestBody LoginDto loginDto) {
        user user = authenticationService.Login(loginDto.getEmail(),loginDto.getPassword());
     return ResponseEntity.ok(user);
    }
    @GetMapping ("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam String token) {
        try {
            if (authenticationService.valityUser(token)){
            return ResponseEntity.ok("Account verified successfully");}
            else {            return ResponseEntity.badRequest().body("token is not valid");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("validation in not working");
        }
    }

}
