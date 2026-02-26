package com.example.aslapp_backend.sevices;


import com.example.aslapp_backend.Exeption.BusinessException;
import com.example.aslapp_backend.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
@Service
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public Long extraIssuedAt(String token){return  extractClaim(token, Claims::getIssuedAt).getTime();}
    private  <T> T extractClaim(String token , Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public String generateToken(User user){

        return generateToken(user,new HashMap<>(), 15 * 60 * 1000);
    }
   public String generateToken(User user, Map<String,Object> claims, int jwtExpiration){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new java.util.Date(System.currentTimeMillis()))
                .setExpiration(new java.util.Date(System.currentTimeMillis() + this.jwtExpiration))
                .signWith(SignatureAlgorithm.HS256, getSignInKey())
                .compact();
    }
    public String generateValidToken(User user, Map<String,Object> claims, long jwtExpiration){

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new java.util.Date(System.currentTimeMillis()))
                .setExpiration(new java.util.Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(SignatureAlgorithm.HS256, getSignInKey())
                .compact();
    }

    public boolean isTokenValid(String token, User user) {
        final String username = extractUsername(token);
        return (username.equals(user.getEmail())) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    public Claims extractAllClaims(String token){

        try{
            return Jwts
                    .parser()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        }catch (ExpiredJwtException e){
          // throw  new BusinessException(HttpStatus.BAD_REQUEST,"Jwt is expired");
            return e.getClaims();
        }

    }

    private Key getSignInKey() {
        byte[] keyBytes ;
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
           keyBytes = digest.digest(secretKey.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,"Jwt is expired");
        }

       // keyBytes =Arrays.copyOf(keyBytes, 32);

        return Keys.hmacShaKeyFor(keyBytes);
    }

}
