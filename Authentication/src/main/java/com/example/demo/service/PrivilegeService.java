package com.example.demo.service;

import com.example.demo.Model.Admin;
import com.example.demo.Model.Privilege;
import com.example.demo.Repository.AdminRepository;
import com.example.demo.Repository.PrivilegeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.*;

@Service
public class PrivilegeService {

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private AdminRepository adminRepository;

    // 🔹 Minor Admin Requests Privilege (No Token Yet)
    public void requestPrivileges(Long minorAdminId) {
        Admin minorAdmin = adminRepository.findById(minorAdminId)
                .orElseThrow(() -> new RuntimeException("❌ Minor Admin not found"));

        Optional<Privilege> existingRequest = privilegeRepository.findByMinorAdminId(minorAdminId);
        if (existingRequest.isPresent()) {
            throw new RuntimeException("❌ Privilege request already exists.");
        }

        Privilege privilege = new Privilege();
        privilege.setMinorAdmin(minorAdmin);
        privilege.setExpirationTime(null); // No token yet, awaiting approval

        privilegeRepository.save(privilege);
        System.out.println("✅ Privilege request recorded.");
    }


    public String approvePrivilegeRequest(Long minorAdminId) {
        Admin minorAdmin = adminRepository.findById(minorAdminId)
                .orElseThrow(() -> new RuntimeException("❌ Minor Admin not found"));

        Optional<Privilege> existingRequest = privilegeRepository.findByMinorAdminId(minorAdminId);
        if (existingRequest.isEmpty()) {
            throw new RuntimeException("❌ No pending privilege request found.");
        }

        Privilege privilege = existingRequest.get();
        String token = UUID.randomUUID().toString(); // Generate a unique token
        privilege.setToken(token);
        privilege.setExpirationTime(LocalDateTime.now().plusHours(2)); // Set token validity

        privilegeRepository.save(privilege);
        return token; // Return the generated token
    }

    // 🔹 Validate Token for Minor Admin
    public boolean validateToken(Long minorAdminId, String token) {
        Optional<Privilege> privilegeOpt = privilegeRepository.findByMinorAdminId(minorAdminId);

        if (privilegeOpt.isEmpty()) {
            return false; // No privilege found
        }

        Privilege privilege = privilegeOpt.get();
        return privilege.getToken() != null &&
               privilege.getToken().equals(token) &&
               privilege.getExpirationTime().isAfter(LocalDateTime.now());
    }

}






