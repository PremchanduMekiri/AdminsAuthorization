package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Model.Admin;
import com.example.demo.Model.Role;
import com.example.demo.Repository.AdminRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/create")
    public ResponseEntity<?> createAdmin(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String role) {

        if (adminRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body("Username already taken.");
        }

        if (!role.equalsIgnoreCase("MAJOR_ADMIN") && !role.equalsIgnoreCase("MINOR_ADMIN")) {
            return ResponseEntity.badRequest().body("Invalid role.");
        }

        // ✅ **Encode password before saving**
        String encodedPassword = passwordEncoder.encode(password);
        Admin admin = new Admin(username, encodedPassword, Role.valueOf(role.toUpperCase()));
        adminRepository.save(admin);

        return ResponseEntity.ok(Map.of(
            "message", "Admin created successfully!",
            "username", admin.getUsername(),
            "role", admin.getRole().name()
        ));
    }

}

