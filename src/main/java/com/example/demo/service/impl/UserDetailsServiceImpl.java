package com.example.demo.service.impl;

import com.example.demo.customError.PasswordMismatchException;
import com.example.demo.customError.UserNotValidException;
import com.example.demo.database.entity.User;
import com.example.demo.database.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findUserByUsername(username);
        if (userOptional.isEmpty()) {
            throw new UserNotValidException("User is not valid");
        }
        User user = userOptional.get();

        // Kiểm tra trạng thái người dùng
        if (!"Active".equalsIgnoreCase(user.getStatus())) {
            throw new UserNotValidException("User is not active");
        }

        Set<GrantedAuthority> authorities = new HashSet<>();
        SimpleGrantedAuthority simpleGranted = new SimpleGrantedAuthority("ROLE_" + userOptional.get().getRole().toUpperCase());
        authorities.add(simpleGranted);

        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(userOptional.get().getPassword())
                .authorities(authorities)
                .build();
    }
}
