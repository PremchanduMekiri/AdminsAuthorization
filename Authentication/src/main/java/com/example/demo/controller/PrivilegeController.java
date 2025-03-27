package com.example.demo.controller;

import com.example.demo.Model.Privilege;
import com.example.demo.service.PrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/privileges")
public class PrivilegeController {

    @Autowired
    private PrivilegeService privilegeService;

    // 🔹 Major Admin grants privileges to Minor Admin
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
}

