package com.example.demo.Model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "privileges")
@Data
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "minor_admin_id", nullable = false)
    private Admin minorAdmin;

    @Column(unique = true)
    private String token;

    @Column
    private LocalDateTime expirationTime; // ✅ Can be NULL before approval

    // ✅ Check if the privilege is still valid
    public boolean isPrivilegeValid() {
        return expirationTime != null && expirationTime.isAfter(LocalDateTime.now());
    }
}


