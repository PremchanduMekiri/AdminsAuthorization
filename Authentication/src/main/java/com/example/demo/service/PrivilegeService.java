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

    // 🔹 Major Admin grants privilege to Minor Admin
    @Transactional
    public String grantPrivileges(Long minorAdminId) {
        Admin minorAdmin = adminRepository.findById(minorAdminId)
                .orElseThrow(() -> new RuntimeException("Minor Admin not found"));

        // Generate JWT token valid for 2 hours (Only for Minor Admin privileges)
        String token = JwtUtil.generateToken(minorAdmin.getUsername(), "MINOR_ADMIN", 2 * 60 * 60 * 1000);
        Date expirationTime = new Date(System.currentTimeMillis() + 2 * 60 * 60 * 1000);

        // Save privilege details in the database
        Privilege privilege = new Privilege(minorAdmin, token, expirationTime);
        privilegeRepository.save(privilege);

        return token;
    }

    // 🔹 Get Privilege by Minor Admin ID
    public Optional<Privilege> getPrivilege(Long minorAdminId) {
        return privilegeRepository.findByAdminId(minorAdminId);
    }

    // 🔹 Revoke Privileges (if needed)
    @Transactional
    public void revokePrivileges(Long minorAdminId) {
        Optional<Privilege> privilege = privilegeRepository.findByAdminId(minorAdminId);
        privilege.ifPresent(privilegeRepository::delete);
    }
}

