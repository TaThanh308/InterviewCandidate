package com.example.demo.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserListDto {
    private Integer id;
    private String username;
    private String email;
    private String phone;
    private String role;
    private String status;
}
