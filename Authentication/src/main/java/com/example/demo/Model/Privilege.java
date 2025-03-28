package com.example.demo.Model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "privileges")
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "minor_admin_id", nullable = false)
    private Admin minorAdmin;

    @Column(nullable = false, unique = true)
    private String token;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date expirationTime;

    public Privilege() {}

    public Privilege(Admin minorAdmin, String token, Date expirationTime) {
        this.minorAdmin = minorAdmin;
        this.token = token;
        this.expirationTime = expirationTime;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Admin getMinorAdmin() {
        return minorAdmin;
    }

    public void setMinorAdmin(Admin minorAdmin) {
        this.minorAdmin = minorAdmin;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    // Helper method to check if the privilege is still valid
    public boolean isPrivilegeValid() {
        return expirationTime.after(new Date());
    }
}
