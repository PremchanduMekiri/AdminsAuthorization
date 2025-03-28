package com.example.demo.service;

import com.example.demo.Model.Admin;
import com.example.demo.Model.Privilege;
import com.example.demo.Repository.AdminRepository;
import com.example.demo.Repository.PrivilegeRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class PrivilegeService {

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private AdminRepository adminRepository;

    // 🔹 Minor Admin requests privileges from Major Admin
    public void requestPrivileges(Long minorAdminId) {
        Admin minorAdmin = adminRepository.findById(minorAdminId)
                .orElseThrow(() -> new RuntimeException("Minor Admin not found"));

        // ✅ Create a pending privilege request
        Privilege privilegeRequest = new Privilege(minorAdmin, null, null); // No token yet
        privilegeRepository.save(privilegeRequest);
    }

    // 🔹 Major Admin approves the privilege request and generates a token
    public String approvePrivilegeRequest(Long minorAdminId) {
        Admin minorAdmin = adminRepository.findById(minorAdminId)
                .orElseThrow(() -> new RuntimeException("Minor Admin not found"));

        // Check if a request exists
        Optional<Privilege> existingRequest = privilegeRepository.findByAdminId(minorAdminId);

        if (existingRequest.isEmpty()) {
            throw new RuntimeException("No pending privilege request found.");
        }

        // Generate a new JWT token valid for 2 hours
        String token = JwtUtil.generateToken(minorAdmin.getUsername(), "MINOR_ADMIN", 2 * 60 * 60 * 1000);
        Date expirationTime = new Date(System.currentTimeMillis() + 2 * 60 * 60 * 1000);

        // ✅ Update privilege details in the database
        Privilege privilege = existingRequest.get();
        privilege.setToken(token);
        privilege.setExpirationTime(expirationTime);
        privilegeRepository.save(privilege);

        return token;
    }

    // 🔹 Major Admin grants privilege directly (without request)
    public String grantPrivileges(Long minorAdminId) {
        Admin minorAdmin = adminRepository.findById(minorAdminId)
                .orElseThrow(() -> new RuntimeException("Minor Admin not found"));

        // Check for existing privileges
        Optional<Privilege> existingPrivilege = privilegeRepository.findByAdminId(minorAdminId);

        if (existingPrivilege.isPresent()) {
            // If token is expired, delete old privilege
            if (existingPrivilege.get().getExpirationTime().before(new Date())) {
                privilegeRepository.delete(existingPrivilege.get());
            } else {
                // ✅ Return the existing valid token instead of generating a new one
                return existingPrivilege.get().getToken();
            }
        }

        // Generate a new JWT token valid for 2 hours
        String token = JwtUtil.generateToken(minorAdmin.getUsername(), "MINOR_ADMIN", 2 * 60 * 60 * 1000);
        Date expirationTime = new Date(System.currentTimeMillis() + 2 * 60 * 60 * 1000);

        // Save privilege details in the database
        Privilege privilege = new Privilege(minorAdmin, token, expirationTime);
        privilegeRepository.save(privilege);

        return token;
    }

    // 🔹 Get Privilege by Minor Admin ID
    public Optional<Privilege> getPrivilege(Long minorAdminId) {
        return privilegeRepository.findByAdminId(minorAdminId)
                .filter(privilege -> privilege.getExpirationTime().after(new Date())); // ✅ Ensure it's not expired
    }

    // 🔹 Revoke Privileges (if needed)
    @Transactional
    public void revokePrivileges(Long minorAdminId) {
        privilegeRepository.findByAdminId(minorAdminId).ifPresent(privilegeRepository::delete);
    }
}



