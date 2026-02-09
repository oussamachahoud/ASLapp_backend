package com.example.aslapp_backend.sevices;

import com.example.aslapp_backend.DTOs.SignupDto;
import com.example.aslapp_backend.DTOs.userDTO;
import com.example.aslapp_backend.Exeption.BusinessException;
import com.example.aslapp_backend.event.UserRegisteredEvent;
import com.example.aslapp_backend.models.Enum.ERole;
import com.example.aslapp_backend.models.Role;
import com.example.aslapp_backend.models.user;
import com.example.aslapp_backend.repositories.RoleRepository;
import com.example.aslapp_backend.repositories.UserRepository;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
@AllArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private  final JwtService jwtService;
    private final ApplicationEventPublisher eventPublisher;
    private final RoleRepository roleRepository;
    private final UserService userService;




    public user Signup(SignupDto signupDto) throws MessagingException, IOException {
        user user = new user(signupDto.getUsername(), passwordEncoder.encode(signupDto.getPassword()),  signupDto.getEmail(), signupDto.getAge());
        user.setEnabled(false);
        if (userRepository.existsByEmail(signupDto.getEmail())) {
            throw new BusinessException(HttpStatus.CONFLICT, "Email already exists");
        }
        if (roleRepository.existsByname(ERole.ROLE_ADMIN.toString())) {
            user.setRoles( roleRepository.findByname(ERole.ROLE_USER.toString()).orElseThrow(() -> new RuntimeException("Role Not Found")));
        }else {
            user.setRoles(new Role(ERole.ROLE_USER));
        }
        String token = jwtService.generateValidToken(user,new HashMap<>(), 15*60*1000);
        user usersave = userService.saveUser(user);
        eventPublisher.publishEvent(new UserRegisteredEvent(usersave,token));
        // emailService.sendEmail(user,token);
        return usersave;
    }

    public String Login(String email, String password) throws MessagingException, IOException {

        user user = userRepository.findByEmail(email)
                .orElseThrow(()->  new BusinessException(HttpStatus.NOT_FOUND, "User not found"));
        if(!user.isEnabled() || !passwordEncoder.matches(password,user.getPassword())){
          throw   new BusinessException(HttpStatus.NOT_FOUND, "User not found");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password,
                        user.getAuthorities()
                )
        );
        String token = jwtService.generateValidToken(user,new HashMap<>(), 15*60*1000);

        return token;
    }

    public boolean valityUser(String jwt){
        String username = jwtService.extractUsername(jwt);
        user user = userRepository.findByusername(username).orElseThrow(()-> new BusinessException(HttpStatus.NOT_FOUND, "User not found"));
         if (jwtService.isTokenValid(jwt,user)){
           user.setEnabled(true);
           userRepository.save(user);
           return true;
         }else {
         return false;}
    }
}
