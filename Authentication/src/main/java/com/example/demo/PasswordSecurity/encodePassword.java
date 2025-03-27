package com.example.demo.PasswordSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class encodePassword {
    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Generate hashed passwords
        String hashedPassword1 = encoder.encode("major123");
        String hashedPassword2 = encoder.encode("minor123");
        
        System.out.println("Hashed Password for Major Admin: " + hashedPassword1);
        System.out.println("Hashed Password for Minor Admin: " + hashedPassword2);
    }

}
