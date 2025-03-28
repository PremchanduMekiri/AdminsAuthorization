package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Model.Admin;
import com.example.demo.Model.Privilege;
import com.example.demo.Model.Role;
import com.example.demo.Repository.AdminRepository;
import com.example.demo.Repository.PrivilegeRepository;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        System.out.println("🔹 Login API hit! Username: " + username);

        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println("❌ User not found: " + username);
                    return new RuntimeException("User not found");
                });

        System.out.println("✅ User found: " + admin.getUsername());
        System.out.println("🔑 Hashed password in DB: " + admin.getPassword());
        System.out.println("🔐 Raw password entered: " + password);

        if (!passwordEncoder.matches(password, admin.getPassword())) {
            System.out.println("❌ Password mismatch!");
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        System.out.println("✅ Password matched!");

        // ✅ Major Admin logs in with full access
        if (admin.getRole() == Role.MAJOR_ADMIN) {
            return ResponseEntity.ok("Major Admin authenticated successfully.");
        }

        // ✅ Minor Admin logs in, check for privilege token
        Optional<Privilege> privilege = privilegeRepository.findByAdminId(admin.getId());

        if (privilege.isPresent() && privilege.get().getExpirationTime().after(new Date())) {
            return ResponseEntity.ok("Login successful! Token: " + privilege.get().getToken());
        }

        // ❌ No privilege, but Minor Admin can still log in
        return ResponseEntity.ok("Login successful, but you don't have privileges yet. Request them from Major Admin.");
    }

    // 🔹 Minor Admin requests privileges
    @PostMapping("/request-privilege")
    public ResponseEntity<String> requestPrivilege(@RequestParam Long minorAdminId) {
        Optional<Admin> minorAdmin = adminRepository.findById(minorAdminId);

        if (minorAdmin.isEmpty() || minorAdmin.get().getRole() != Role.MINOR_ADMIN) {
            return ResponseEntity.badRequest().body("Invalid Minor Admin ID.");
        }

        return ResponseEntity.ok("Privilege request sent to Major Admin. Wait for approval.");
    }
}







