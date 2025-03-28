package com.example.demo.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    
    private static final String SECRET_KEY = "YourSuperSecretKeyForJWTYourSuperSecretKeyForJWT"; // ✅ 256-bit key required
    private static final Key SIGNING_KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes()); // ✅ Secure signing key

    // 🔹 Generates JWT Token with role-based expiration
    public static String generateToken(String username, String role, long expirationMs) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(SIGNING_KEY, SignatureAlgorithm.HS256) // ✅ Uses secure key
                .compact();
    }

    // 🔹 Extracts claims safely
    public static Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SIGNING_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expired, please request a new privilege.");
        } catch (Exception e) {
            throw new RuntimeException("Invalid Token.");
        }
    }

    // 🔹 Checks if token is expired
    public static boolean isTokenExpired(String token) {
        try {
            return extractClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return true; // Token is expired or invalid
        }
    }
}
