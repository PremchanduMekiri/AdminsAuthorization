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

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private Date expirationTime;

    public Privilege() {}

    public Privilege(Admin minorAdmin, String token, Date expirationTime) {
        this.minorAdmin = minorAdmin;
        this.token = token;
        this.expirationTime = expirationTime;
    }

    // Getters and Setters
}
