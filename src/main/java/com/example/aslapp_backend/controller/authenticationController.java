package com.example.aslapp_backend.controller;

import com.example.aslapp_backend.DTOs.requestDTOs.LoginDto;
import com.example.aslapp_backend.DTOs.requestDTOs.SignupDto;
import com.example.aslapp_backend.Exeption.BusinessException;
import com.example.aslapp_backend.models.User;
import com.example.aslapp_backend.sevices.AuthenticationService;
import com.example.aslapp_backend.sevices.JwtService;
import com.example.aslapp_backend.sevices.RefreshTokenService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Authentication", description = "Signup, login, email verification and logout")
@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@SecurityRequirements
public class authenticationController {

    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;
    private  final JwtService jwtService;

    @Operation(summary = "Register a new user", description = "Creates a new user account and sends a verification email")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Signup successful – verification email sent"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Email already in use")
    })
    @SneakyThrows
    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@Valid @RequestBody SignupDto signupDto) {
    authenticationService.Signup(signupDto);
        return ResponseEntity.status(201).body(Map.of("message", "Signup successful. Please check your email to verify your account."));   }

    @Operation(summary = "Log in", description = "Authenticates the user and sets a JWT HttpOnly cookie")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Login successful – JWT cookie set"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @SneakyThrows
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto, HttpServletRequest request, HttpServletResponse response) {
        User user = authenticationService.Login(loginDto.getEmail(),loginDto.getPassword());
        String device = request.getHeader("User-Agent");
        String ip = request.getRemoteAddr();
        String jti = UUID.randomUUID().toString();


        String accessToken = jwtService.generateValidToken(user,new HashMap<>(), 120*60*1000);
        String refreshToken = jwtService.generateValidToken(user, Map.of("jti",jti), 7 * 24 * 60 * 60*1000);
        refreshTokenService.storeRefreshToken(user.getId(),jti, device, ip);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/users/me"));
        headers.set("X-Entity-Version", "1.0");
        headers.add(HttpHeaders.SET_COOKIE, createCookie("access_token",accessToken,"/",Duration.ofMinutes(120)).toString());
        headers.add(HttpHeaders.SET_COOKIE, createCookie("refresh_token",refreshToken,"/api/auth/refresh",Duration.ofDays(7)).toString());


     return new ResponseEntity<>(
             headers,
             HttpStatus.CREATED
             );

    }

    @Operation(summary = "Verify email", description = "Verifies the user's email address using the token sent by email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account verified successfully"),
            @ApiResponse(responseCode = "400", description = "Token is not valid or expired")
    })
    @GetMapping("/verify")
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



    @Operation(summary = "refresh token", description = "refresh the access JWT cookie")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "refresh token successful")
    })
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request,@Parameter(hidden = true) @CookieValue(value = "refresh_token", required = false) String refreshToken) {
        if (refreshToken == null) throw new BusinessException(HttpStatus.UNAUTHORIZED,"Unauthorized");
        if (jwtService.isTokenExpired(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String device = request.getHeader("User-Agent");
        String ip = request.getRemoteAddr();

        try {
            Claims claims = jwtService.extractAllClaims(refreshToken);
            User user = authenticationService.findIdByEmail(claims.getSubject());
            String jti =refreshTokenService.rotateRefreshToken(user.getId(),(String) claims.get("jti"),device,ip);
            String accessToken = jwtService.generateValidToken(user, Map.of("jti",jti), 120 * 60*1000);
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, createCookie("access_token",accessToken,"/",Duration.ofMinutes(120)).toString())
                    .build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

    }
    @Operation(summary = "Log out", description = "Clears the JWT cookie")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logout successful")
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@Parameter(hidden = true)HttpServletResponse response,@Parameter(hidden = true) @CookieValue(value = "refresh_token", required = false) String refreshToken) {
        if (refreshToken == null) throw new BusinessException(HttpStatus.UNAUTHORIZED,"Unauthorized");
        try {
            Claims claims = jwtService.extractAllClaims(refreshToken);
            User user = authenticationService.findIdByEmail(claims.getSubject());
            refreshTokenService.deleteRefreshToken(user.getId(),(String) claims.get("jti"));
            refreshTokenService.blacklistToken((String) claims.get("jti"));
            response.addHeader(HttpHeaders.SET_COOKIE,createCookie("refresh_token","","/api/auth/refresh",Duration.ZERO).toString());
            response.addHeader(HttpHeaders.SET_COOKIE,createCookie("access_token","","",Duration.ZERO).toString());
            return ResponseEntity.ok(Map.of("message", "Logout successful"));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

    }

    @Operation(summary = "Log out all sesion", description = "Clears the JWT cookie")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logout successful")
    })
    @PostMapping("/logoutall")
    public ResponseEntity<?> logoutAllSesion(@Parameter(hidden = true) HttpServletResponse response,
        @Parameter(hidden = true) @CookieValue(value = "refresh_token", required = false) String refreshToken) {
        if (refreshToken == null) throw new BusinessException(HttpStatus.UNAUTHORIZED,"Unauthorized");
        try {
            Claims claims = jwtService.extractAllClaims(refreshToken);
            User user = authenticationService.findIdByEmail(claims.getSubject());
            refreshTokenService.deleteAllRefreshTokens(user.getId());            refreshTokenService.blacklistToken((String) claims.get("jti"));
            response.addHeader(HttpHeaders.SET_COOKIE,createCookie("refresh_token","","/",Duration.ZERO).toString());
            response.addHeader(HttpHeaders.SET_COOKIE,createCookie("access_token","","/api/auth/refresh",Duration.ZERO).toString());
            return ResponseEntity.ok(Map.of("message", "Logout successful"));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

    }

    private ResponseCookie createCookie(String type, String token,String path,Duration duration) {
        return ResponseCookie.from(type, token)
                .httpOnly(true)
                .secure(true)
                .path(path)
                .maxAge(duration)
                .sameSite("Lax")
                .build();
    }


}