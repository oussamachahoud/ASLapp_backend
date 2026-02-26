package com.example.aslapp_backend.Config;

import com.example.aslapp_backend.models.Role;
import com.example.aslapp_backend.models.User;
import com.example.aslapp_backend.repositories.UserRepository;
import com.example.aslapp_backend.sevices.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtauthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserRepository userRepository;





    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest Request,
            @NotNull HttpServletResponse Response,
            @NotNull FilterChain filterChain
            ) throws ServletException , IOException {
            String jwt = extractJwtFromCookie(Request);

        if (jwt == null ) {
            filterChain.doFilter(Request, Response);
            return;
        }

        try {
            final String username = jwtService.extractUsername(jwt);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (username !=null && auth == null){

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                User u= userRepository.findByEmail(username).orElseThrow(
                        () -> new UsernameNotFoundException("User not found")
                );

                 if(jwtService.isTokenValid(jwt,u)){

                      UsernamePasswordAuthenticationToken  authenTocken =  new UsernamePasswordAuthenticationToken(
                             userDetails,
                             null,
                              u.getAuthorities()
                           //   Collections.singleton(new SimpleGrantedAuthority(ROLE_ADMIN.toString()))
                     );
                      authenTocken.setDetails(new WebAuthenticationDetails(Request));
                      SecurityContextHolder.getContext().setAuthentication(authenTocken);
                 }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        filterChain.doFilter(Request,Response);
    }

    private String  extractJwtFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
    private String extractJwtFromBearerjwt(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer:")) {
            return null;
        }
        String jwt = authHeader.substring(7);
        return jwt;
    }


}