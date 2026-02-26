package com.example.aslapp_backend.sevices;

import com.example.aslapp_backend.models.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {
     private JwtService jwtService;
     private Map<String, Object> claims = new HashMap<>();
     @BeforeEach
     void intial(){
         jwtService = new JwtService();
         ReflectionTestUtils.setField(jwtService,"secretKey","oussama");
         ReflectionTestUtils.setField(jwtService, "jwtExpiration", 15 * 60 * 1000L);
     }
    private User user(String username) {

        return User.builder().username(username)
                .password("ignored")
                .roles(null)
                .build();
    }
    @Test
    void should_Token_if_has_1_user(){
    // Arrange
    User u = user("oussama@mail.com");
    // Act
    String token = jwtService.generateValidToken(u, claims, 60_000L); // 1 min
    String extracted = jwtService.extractUsername(token);
    // Assert
    assertThat(extracted).isEqualTo("oussama@mail.com");
    }
    @Test
    void Test_if_token_not_valid_for_2_user(){
         //Arrange
        User u1 = user("oussama@mail.com");
        User u2 = user("Ahmed@mail.com");
        //Act
        String token = jwtService.generateValidToken(u1, claims, 60_000L);
        boolean valid = jwtService.isTokenValid(token, u2);
        //Assert
        assertThat(valid).isFalse();

    }
    @Test
    void Test_if_token_respact_timeEpiration(){
        //Arrange
        User u1 = user("oussama@mail.com");
        log.debug("hello , i test this log");
        //Act
        String token = jwtService.generateValidToken(u1, claims, -1L);
        boolean valid = jwtService.isTokenValid(token, u1);
        //Assert
        assertThat(valid).isFalse();
    }
    @Test
    void Print_claims(){
        //Arrange
        User u1 = user("oussama@mail.com");
        //Act
        String token = jwtService.generateValidToken(u1, claims, 60_000L);
        boolean valid = jwtService.isTokenExpired(token);
        //Assert
        assertThat(valid).isFalse();
    }

    @Test
    void extractUsername_evenIfTokenExpired_stillReturnsSubject() {
        // هذا يختبر منطقك في extractAllClaims: أنت تعمل catch ExpiredJwtException وترجع claims
        User u = user("expired@mail.com");

        String token = jwtService.generateValidToken(u, claims, -1000L);

        String extracted = jwtService.extractUsername(token);

        assertThat(extracted).isEqualTo("expired@mail.com");
    }
    @Test
    void tokenSignedWithDifferentSecret_shouldFailValidation() {
        // Arrange
        User u = user("User@mail.com");
        String token = jwtService.generateValidToken(u, Map.of(), 60_000L);

        // Service آخر بمفتاح مختلف
        JwtService other = new JwtService();
        ReflectionTestUtils.setField(other, "secretKey", "another-secret-KEY-not-the-same-999999999");
        ReflectionTestUtils.setField(other, "jwtExpiration", 15 * 60 * 1000L);

        // Act + Assert
        // extractAllClaims سيحاول parsing بمفتاح غلط => عادة يرمي exception (غير ExpiredJwtException)
        assertThatThrownBy(() -> other.extractUsername(token))
                .isInstanceOf(Exception.class);
    }


}
