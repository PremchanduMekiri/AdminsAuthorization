package com.example.demo.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "admins")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)  
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean requestedPrivilege = false;  // ✅ Tracks if Minor Admin requested privileges

    @Column(nullable = false)
    private boolean privilegeGranted = false;  // ✅ Tracks if Major Admin approved privileges

    // Constructors
    public Admin() {}

    public Admin(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.requestedPrivilege = false;
        this.privilegeGranted = false;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isRequestedPrivilege() {
        return requestedPrivilege;
    }

    public void setRequestedPrivilege(boolean requestedPrivilege) {
        this.requestedPrivilege = requestedPrivilege;
    }

    public boolean isPrivilegeGranted() {
        return privilegeGranted;
    }

    public void setPrivilegeGranted(boolean privilegeGranted) {
        this.privilegeGranted = privilegeGranted;
    }
}



