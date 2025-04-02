package com.example.demo.controller;

import com.example.demo.Model.Admin;
import com.example.demo.Model.Privilege;
import com.example.demo.Model.PrivilegeRequestDto;
import com.example.demo.Model.Role;
import com.example.demo.Repository.AdminRepository;
import com.example.demo.Repository.PrivilegeRepository;
import com.example.demo.service.PrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.validation.Valid;  // ✅ Newer versions (Spring Boot 3+)



import java.sql.Date;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/privileges")
public class PrivilegeController {

    @Autowired
    private PrivilegeService privilegeService;

    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private PrivilegeRepository privilegerepo;
    

    @Autowired
    private PasswordEncoder passwordEncoder; 

    @PostMapping("/request")
    public ResponseEntity<String> requestPrivileges(@RequestBody PrivilegeRequestDto request) {
        System.out.println("🔥 requestPrivileges endpoint hit!");

        if (request.getUsername() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body("❌ Missing username or password.");
        }

        // Authenticate Minor Admin
        Optional<Admin> optionalAdmin = adminRepository.findByUsername(request.getUsername());
        if (optionalAdmin.isEmpty()) {
            return ResponseEntity.status(401).body("❌ User not found.");
        }

        Admin admin = optionalAdmin.get();
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            return ResponseEntity.status(401).body("❌ Invalid credentials.");
        }

        // Check if the user is a Minor Admin
        if (admin.getRole() != Role.MINOR_ADMIN) {
            return ResponseEntity.status(403).body("❌ Only Minor Admins can request privileges.");
        }

        // Request Privileges
        privilegeService.requestPrivileges(admin.getId());

        return ResponseEntity.ok("✅ Privilege request submitted.");
    }

    


    @PostMapping("/approve")
    public ResponseEntity<String> approvePrivilegeRequest(@RequestParam Long minorAdminId) {
        String token = privilegeService.approvePrivilegeRequest(minorAdminId);
        return ResponseEntity.ok("Privilege request approved! Token: " + token);
    }


    @DeleteMapping("/revoke")
    public ResponseEntity<String> revokePrivileges(@RequestParam Long minorAdminId) {
        privilegeService.requestPrivileges(minorAdminId);
        return ResponseEntity.ok("Privilege revoked successfully!");
    }
}


