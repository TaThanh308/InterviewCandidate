package com.example.demo.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateDto {
    private String name;
    private String username;
    private String password;
    private String email;
    private LocalDate dob;
    private String address;
    private String phone;
    private String gender;
    private String role;
    private String department;
    private String status;
    private String notes;
}
