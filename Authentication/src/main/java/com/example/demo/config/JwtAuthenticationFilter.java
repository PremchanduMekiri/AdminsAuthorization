package com.example.demo.config;

import com.example.demo.service.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String token = request.getHeader("Authorization");

        // Check if token is present and starts with "Bearer "
        if (token == null || !token.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        token = token.substring(7); // Remove "Bearer " prefix

        Claims claims;
        try {
            claims = JwtUtil.extractClaims(token);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid Token");
            return;
        }

        // Extract username and role from JWT
        String username = claims.getSubject();
        String role = claims.get("role", String.class);

        // Create authentication object
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                new User(username, "", Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))),
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
        );

        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(auth);

        chain.doFilter(request, response);
    }
}

