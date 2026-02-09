package com.example.aslapp_backend.sevices;

import com.example.aslapp_backend.DTOs.SignupDto;
import com.example.aslapp_backend.event.UserRegisteredEvent;
import com.example.aslapp_backend.models.Enum.ERole;
import com.example.aslapp_backend.models.Role;
import com.example.aslapp_backend.models.user;
import com.example.aslapp_backend.repositories.RoleRepository;
import com.example.aslapp_backend.repositories.UserRepository;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private EmailService emailService;
    @Mock
    private JwtService jwtService;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void shouldRegisterUserSuccessfully() throws MessagingException, IOException {
        // Arrange
        SignupDto signupDto = new SignupDto("testuser", "password", "test@test.com", 25, "reason");
        user savedUser = new user("oussama", "encodedPass", "test@test.com", 25);
        savedUser.setId(1L);
        Role userRole = new Role(ERole.ROLE_USER);
        
        when(userRepository.existsByEmail(signupDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(signupDto.getPassword())).thenReturn("encodedPass");
        when(roleRepository.existsByname(ERole.ROLE_ADMIN.toString())).thenReturn(true);
        when(roleRepository.findByname(ERole.ROLE_USER.toString())).thenReturn(Optional.of(userRole));
        when(jwtService.generateValidToken(any(user.class), any(), anyLong())).thenReturn("jwt-token");
        when(userService.saveUser(any(user.class))).thenReturn(savedUser);

        // Act
        user result = authenticationService.Signup(signupDto);

        // Assert
        assertNotNull(result);
        assertEquals("oussama", result.getUsername());
        verify(userService).saveUser(any(user.class));
        verify(eventPublisher).publishEvent(any(UserRegisteredEvent.class));
    }

    @Test
    void shouldLoginSuccessfully() throws MessagingException, IOException {
        // Arrange
        String email = "test@test.com";
        String password = "password";
        user user = new user("testuser", "encodedPass", email, 25);
        user.setEnabled(true);
        
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
        when(jwtService.generateValidToken(eq(user), any(), anyLong())).thenReturn("jwt-token");

        // Act
        String token = authenticationService.Login(email, password);

        // Assert
        assertEquals("jwt-token", token);
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void shouldValidateUser() {
        // Arrange
        String token = "jwt-token";
        String username = "testuser";
        user user = new user(username, "pass", "email", 20);
        
        when(jwtService.extractUsername(token)).thenReturn(username);
        when(userRepository.findByusername(username)).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(token, user)).thenReturn(true);

        // Act
        boolean result = authenticationService.valityUser(token);

        // Assert
        assertTrue(result);
        assertTrue(user.getEnabled());
        verify(userRepository).save(user);
    }
}
