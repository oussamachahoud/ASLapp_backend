package com.example.aslapp_backend.sevices;

import com.example.aslapp_backend.DTOs.SignupDto;
import com.example.aslapp_backend.event.UserRegisteredEvent;
import com.example.aslapp_backend.models.ERole;
import com.example.aslapp_backend.models.Role;
import com.example.aslapp_backend.models.user;
import com.example.aslapp_backend.repositories.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private  final JwtService jwtService;
    private final UserSave userSave;
    private final ApplicationEventPublisher eventPublisher;


    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            EmailService emailService
            , JwtService jwtService,
            UserSave userSave
            , ApplicationEventPublisher eventPublisher

    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.userSave = userSave;
        this.eventPublisher = eventPublisher;
    }

    public user Signup(SignupDto signupDto) throws MessagingException, IOException {
        user user = new user(signupDto.getUsername(), passwordEncoder.encode(signupDto.getPassword()), signupDto.getReason(), signupDto.getEmail(), signupDto.getAge());
        user.setEnabled(false);
        if (userRepository.existsByEmail(signupDto.getEmail())) {
            throw new  RuntimeException("Email Already Exists");

        }

        user.setRoles(new Role(ERole.ROLE_USER));
        String token = jwtService.generateValidToken(user,new HashMap<>(), 15*60*1000);
        user usersave = userSave.saveUser(user);
        eventPublisher.publishEvent(new UserRegisteredEvent(usersave,token));
       // emailService.sendEmail(user,token);

        return userSave.saveUser(user);
    }
    public user Login(String email, String password) throws MessagingException, IOException {

        user user = userRepository.findByEmail(email)
                .orElseThrow(()->  new UsernameNotFoundException("user nor Found"));
        if(!user.isEnabled() || !passwordEncoder.matches(password,user.getPassword())){
          throw   new RuntimeException("user nor valid");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password,
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
