package com.expensetracker.user_service.security;

import com.expensetracker.user_service.Service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * JwtAuthenticationFilter is responsible for:
 * 1. Extracting the JWT token from the request header.
 * 2. Validating the token.
 * 3. Setting the authentication object in the SecurityContext.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Get the Authorization header
        String header = request.getHeader("Authorization");
        System.out.println("User Service JwtAuthenticationFilter: Authorization header = " + header);

        // Check if header has a Bearer token
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7); // remove "Bearer "
            String username = jwtUtil.extractUsername(token); // extract username from JWT

            // If username is valid and SecurityContext is empty
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Validate the token
                if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                    // Create authentication token
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );

                    // Set authentication in context
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}
