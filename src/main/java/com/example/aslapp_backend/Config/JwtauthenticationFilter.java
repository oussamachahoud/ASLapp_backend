package com.example.aslapp_backend.Config;

import com.example.aslapp_backend.sevices.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
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
public class JwtauthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public JwtauthenticationFilter(UserDetailsService userDetailsService, JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;

    }



    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest Request,
            @NotNull HttpServletResponse Response,
            @NotNull FilterChain filterChain
            ) throws ServletException , IOException {
        final String authenheader= Request.getHeader("Authorization");
        if(authenheader == null  || !authenheader.startsWith("Bearer")){
            filterChain.doFilter(Request,Response);
            return;
        }

        try {
            final String jwt = authenheader.substring(7);
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
