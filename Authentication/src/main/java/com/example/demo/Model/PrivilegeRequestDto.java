package com.example.demo.Model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PrivilegeRequestDto {
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @NotBlank(message = "Password cannot be empty")
    private String password;


}
