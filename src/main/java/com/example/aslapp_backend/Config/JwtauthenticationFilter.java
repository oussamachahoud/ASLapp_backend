package com.example.aslapp_backend.Config;

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
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtauthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;





    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest Request,
            @NotNull HttpServletResponse Response,
            @NotNull FilterChain filterChain
            ) throws ServletException , IOException {
        // Bearer jwt
//        final String authHeader = Request.getHeader("Authorization");
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//        jwt = authHeader.substring(7);

        // cookie jwt
        String jwt = null;
        if (Request.getCookies() != null) {
            for (Cookie cookie : Request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        if (jwt == null && !jwtService.isTokenExpired(jwt)) {
            filterChain.doFilter(Request, Response);
            return;
        }

        try {
            final String username = jwtService.extractUsername(jwt);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (username !=null && auth == null){
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                 if(jwtService.isTokenValid(jwt,userDetails)){

                      UsernamePasswordAuthenticationToken  authenTocken =  new UsernamePasswordAuthenticationToken(
                             userDetails,
                             null,
                             userDetails.getAuthorities()
                     );
                      authenTocken.setDetails(new WebAuthenticationDetails(Request));
                      SecurityContextHolder.getContext().setAuthentication(authenTocken);
                 }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        filterChain.doFilter(Request,Response);



    }


}