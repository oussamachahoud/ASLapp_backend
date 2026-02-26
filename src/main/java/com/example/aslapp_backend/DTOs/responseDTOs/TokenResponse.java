package com.example.aslapp_backend.DTOs.responseDTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO containing access and refresh tokens
 * 
 * Example Response:
 * {
 *   "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *   "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *   "tokenType": "Bearer",
 *   "accessTokenExpiresIn": 900000,
 *   "refreshTokenExpiresIn": 604800000
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResponse {
    
    /**
     * JWT access token (short-lived: 15 minutes)
     * Used for API requests in Authorization header: "Bearer {accessToken}"
     */
    private String accessToken;
    
    /**
     * JWT refresh token (long-lived: 7 days)
     * Used to obtain new access tokens when expired
     * Should be stored in HttpOnly cookie for security
     */
    private String refreshToken;
    
    /**
     * Token type, typically "Bearer"
     * Used in Authorization header as: "Bearer {accessToken}"
     */
    private String tokenType;
    
    /**
     * Access token expiration time in milliseconds
     * Example: 900000 = 15 minutes
     */
    private long accessTokenExpiresIn;
    
    /**
     * Refresh token expiration time in milliseconds
     * Example: 604800000 = 7 days
     */
    private long refreshTokenExpiresIn;
}
