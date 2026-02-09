package com.example.aslapp_backend.controller;

import com.example.aslapp_backend.DTOs.LoginDto;
import com.example.aslapp_backend.DTOs.SignupDto;
import com.example.aslapp_backend.sevices.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class authenticationController {

    private final AuthenticationService authenticationService;


//    {
//        "message": "Signup successful. Please check your email to verify your account."
//    }


    @SneakyThrows
    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@Valid @RequestBody SignupDto signupDto) {
    authenticationService.Signup(signupDto);
        //HttpHeaders headers = new HttpHeaders();
        //headers.setLocation(URI.create("/api/users/" + user.getId()));
        //headers.set("X-Entity-Version", "1.0");

        return ResponseEntity.status(201).body(Map.of("message", "Signup successful. Please check your email to verify your account."));   }

    @SneakyThrows
    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginDto loginDto, HttpServletResponse response) {
        String token = authenticationService.Login(loginDto.getEmail(),loginDto.getPassword());
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/users/me"));
        headers.set("X-Entity-Version", "1.0");
        /*this is for jwt Bearer */
     //   headers.setBearerAuth((String) objectMap.get("token"));

        /*this is for jwt cookie */
        // set token as HttpOnly cookie
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        // set cookie expiry to match token lifetime (7 days here as example)
        cookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(cookie);

     return new ResponseEntity<>(
             /*this is for jwt Bearer */
//             Map.of(
//             "accessToken", token,
//             "tokenType", "Bearer"
//             ),
             headers,
             HttpStatus.CREATED);
    }

    @GetMapping ("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam String token) {
        try {
            if (authenticationService.valityUser(token)) {
                return ResponseEntity.ok(Map.of("message", "Account verified successfully"));
            }
            return ResponseEntity.badRequest().body(Map.of("message", "Token is not valid or expired"));

        } catch (RuntimeException e) {
            throw new RuntimeException("validation in not working");
        }
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(
            HttpServletResponse response
    ){
        // clear the jwt cookie
        Cookie cookie = new Cookie("jwt", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    return ResponseEntity.ok(Map.of(
            "message", "Logout successful"
            ));}

}