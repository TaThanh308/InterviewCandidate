package com.example.demo.model.user;

import com.example.demo.database.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUser extends User {
    private String username;
    private String password;
}

