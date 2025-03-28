package com.example.demo.controller;

import com.example.demo.Model.Admin;
import com.example.demo.Model.Privilege;
import com.example.demo.Repository.AdminRepository;
import com.example.demo.service.PrivilegeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/privileges")
public class PrivilegeController {

    @Autowired
    private PrivilegeService privilegeService;

    @Autowired
    private AdminRepository adminRepository;



        @PostMapping("/request")
        public ResponseEntity<String> requestPrivileges() {
            // 🔹 Get the currently authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(403).body("❌ Unauthorized request.");
            }

            // 🔹 Check if user has ROLE_MINOR_ADMIN
            boolean isMinorAdmin = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_MINOR_ADMIN"));

            if (!isMinorAdmin) {
                return ResponseEntity.status(403).body("❌ Only Minor Admins can request privileges.");
            }

            // 🔹 Find the Minor Admin in the database
            String username = authentication.getName();
            Admin minorAdmin = adminRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("❌ Admin not found."));

            // 🔹 Process privilege request
            privilegeService.requestPrivileges(minorAdmin.getId());
            
            return ResponseEntity.ok("✅ Privilege request sent to Major Admin.");
        }
    


    // 🔹 Major Admin approves the privilege request
    @PostMapping("/approve")
    public ResponseEntity<String> approvePrivilegeRequest(@RequestParam Long minorAdminId) {
        String token = privilegeService.approvePrivilegeRequest(minorAdminId);
        return ResponseEntity.ok("Privilege request approved! Token: " + token);
    }

    // 🔹 Major Admin grants privileges to Minor Admin directly
    @PostMapping("/grant")
    public ResponseEntity<String> grantPrivileges(@RequestParam Long minorAdminId) {
        String token = privilegeService.grantPrivileges(minorAdminId);
        return ResponseEntity.ok("Privilege granted successfully! Token: " + token);
    }

    // 🔹 Get privileges of a Minor Admin
    @GetMapping("/get")
    public ResponseEntity<Optional<Privilege>> getPrivilege(@RequestParam Long minorAdminId) {
        return ResponseEntity.ok(privilegeService.getPrivilege(minorAdminId));
    }

    // 🔹 Major Admin revokes Minor Admin's privileges
    @DeleteMapping("/revoke")
    public ResponseEntity<String> revokePrivileges(@RequestParam Long minorAdminId) {
        privilegeService.revokePrivileges(minorAdminId);
        return ResponseEntity.ok("Privilege revoked successfully!");
    }
    @GetMapping("/test-auth")
    public ResponseEntity<String> testAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok("User: " + authentication.getName() + ", Roles: " + authentication.getAuthorities());
    }
}

