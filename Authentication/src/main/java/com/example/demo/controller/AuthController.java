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
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

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
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("❌ User not found"));

        if (!passwordEncoder.matches(password, admin.getPassword())) {
            return ResponseEntity.status(401).body("❌ Invalid credentials");
        }

        if (admin.getRole() == Role.MAJOR_ADMIN) {
            return ResponseEntity.ok("✅ Major Admin authenticated successfully.");
        }

        // For Minor Admins, check for an approved privilege with a valid token
        Optional<Privilege> privilegeOpt = privilegeRepository.findByMinorAdminId(admin.getId());
        if (privilegeOpt.isPresent()) {
            Privilege privilege = privilegeOpt.get();
            if (privilege.getToken() != null && privilege.getExpirationTime().isAfter(LocalDateTime.now())) {
                return ResponseEntity.ok("✅ Login successful! Use this token: " + privilege.getToken());
            } else {
                return ResponseEntity.status(403).body("❌ Privilege token expired or not granted. Request approval.");
            }
        }

        return ResponseEntity.status(403).body("❌ No privilege found. Request approval from Major Admin.");
    }


}










