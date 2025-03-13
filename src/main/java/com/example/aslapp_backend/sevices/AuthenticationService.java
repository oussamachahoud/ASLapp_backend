package com.example.aslapp_backend.sevices;

import com.example.aslapp_backend.DTOs.SignupDto;
import com.example.aslapp_backend.models.ERole;
import com.example.aslapp_backend.models.Role;
import com.example.aslapp_backend.models.user;
import com.example.aslapp_backend.repositories.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private  final JwtService jwtService;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            EmailService emailService
            , JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.jwtService = jwtService;
    }

    public user Signup(SignupDto signupDto) throws MessagingException, IOException {
        user user = new user( signupDto.getUsername(), passwordEncoder.encode(signupDto.getPassword()), signupDto.getEmail(), signupDto.getAge(), signupDto.getReason());
        user.setEnabled(false);
        if (!userRepository.existsByEmail(signupDto.getEmail())) {
            throw new  RuntimeException("Email Already Exists");
        }
        user.setRoles(new Role(ERole.ROLE_USER));
        userRepository.save(user);
        String token = jwtService.generateValidToken(user,new HashMap<>(), 15*60*1000);
        emailService.sendEmail(user,token);
        return user;
    }
    public user Login(String email, String password) throws MessagingException, IOException {
        user user = userRepository.findByEmail(email)
                .orElseThrow(()->  new UsernameNotFoundException("user nor Found"));
        if(!user.isEnabled() || !passwordEncoder.matches(password,user.getPassword())){
          throw   new RuntimeException();
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        passwordEncoder.encode(user.getPassword()),
                        user.getAuthorities()
                )

        );

        return user;
    }

    public boolean valityUser(String jwt){
        String username = jwtService.extractUsername(jwt);
        user user = userRepository.findByusername(username).orElseThrow(()-> new RuntimeException("user not found"));
         if (jwtService.isTokenValid(jwt,user)){
           user.setEnabled(true);
           userRepository.save(user);
           return true;

         }else {
         return false;}
    }



}
