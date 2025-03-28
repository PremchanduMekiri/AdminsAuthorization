package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // ✅ Only one bean definition
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
            .requestMatchers("/api/auth/login").permitAll()  // ✅ Allow login without authentication
            .requestMatchers("/api/privileges/request").hasAuthority("MINOR_ADMIN") // ✅ Only authenticated Minor Admins can request privileges
            .requestMatchers("/api/privileges/grant", "/api/privileges/approve", "/api/privileges/revoke").hasAuthority("MAJOR_ADMIN") // ✅ Major Admin controls privileges
            .anyRequest().authenticated() // 🔐 All other endpoints require authentication
            .and()
            .httpBasic(); // ✅ Enable basic authentication (username/password in each request)

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}

