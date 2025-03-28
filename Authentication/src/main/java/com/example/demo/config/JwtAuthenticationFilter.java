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
import java.util.Date;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String token = request.getHeader("Authorization");

        // ✅ Check if token is present and starts with "Bearer "
        if (token == null || !token.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        token = token.substring(7); // Remove "Bearer " prefix

        Claims claims;
        try {
            claims = JwtUtil.extractClaims(token);

            // ✅ Check token expiration
            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token Expired");
                return;
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid Token");
            return;
        }

        // ✅ Extract username and role from JWT
        String username = claims.getSubject();
        String role = claims.get("role", String.class);

        // ✅ Allow only Major Admin or Minor Admins with a valid token
        if (role == null || (!role.equals("MAJOR_ADMIN") && !role.equals("MINOR_ADMIN"))) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Access Denied");
            return;
        }

        // ✅ Create authentication object
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                new User(username, "", Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))),
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
        );

        // ✅ Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(auth);

        chain.doFilter(request, response);
    }
}


