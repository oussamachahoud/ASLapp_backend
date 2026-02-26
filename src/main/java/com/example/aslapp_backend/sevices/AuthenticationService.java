package com.example.aslapp_backend.sevices;

import com.example.aslapp_backend.DTOs.requestDTOs.SignupDto;
import com.example.aslapp_backend.Exeption.BusinessException;
import com.example.aslapp_backend.event.UserRegisteredEvent;
import com.example.aslapp_backend.models.Enum.ERole;
import com.example.aslapp_backend.models.Role;
import com.example.aslapp_backend.models.User;
import com.example.aslapp_backend.repositories.RoleRepository;
import com.example.aslapp_backend.repositories.UserRepository;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;

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




    public User Signup(SignupDto signupDto) throws MessagingException, IOException {
        User user = new User(signupDto.getUsername(), passwordEncoder.encode(signupDto.getPassword()),  signupDto.getEmail(), signupDto.getAge());
        user.setEnabled(false);
        if (userRepository.existsByEmail(signupDto.getEmail())) {
            throw new BusinessException(HttpStatus.CONFLICT, "Email already exists");
        }
        boolean b=roleRepository.existsByName(ERole.ROLE_ADMIN);
        System.out.println(b);
        if (b) {
            user.addRole( roleRepository.findByName(ERole.ROLE_USER) .orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_USER))));
        }else {
            user.addRole( roleRepository.findByName(ERole.ROLE_ADMIN) .orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_ADMIN))));
        }
        String token = jwtService.generateValidToken(user,new HashMap<>(), 15*60*1000);
        User usersave = userService.saveUser(user);
        eventPublisher.publishEvent(new UserRegisteredEvent(usersave,token));
        // emailService.sendEmail(User,token);
        return usersave;
    }

    public User Login(String email, String password) throws MessagingException, IOException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(()->  new BusinessException(HttpStatus.NOT_FOUND, "User not found"));
        if(!user.isEnabled() || !passwordEncoder.matches(password,user.getPassword())){
          throw   new BusinessException(HttpStatus.NOT_FOUND, "User is not active or password is incorrect");
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
        String email = jwtService.extractUsername(jwt);
        User user = userRepository.findByEmail(email).orElseThrow(()-> new BusinessException(HttpStatus.NOT_FOUND, "User not found"));
         if (jwtService.isTokenValid(jwt,user)){
           user.setEnabled(true);
           userRepository.save(user);
           return true;
         }else {
         return false;}
    }
    public User findIdByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(
                () -> new BusinessException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
