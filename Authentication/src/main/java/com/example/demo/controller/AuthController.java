package com.example.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Model.Admin;
import com.example.demo.Repository.AdminRepository;
import com.example.demo.service.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AdminRepository adminRepository;

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        // Authenticate user
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        // Fetch admin details
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate JWT token
        return JwtUtil.generateToken(admin.getUsername(), admin.getRole().toString(), 24 * 60 * 60 * 1000); // 24 hours
    }
}

