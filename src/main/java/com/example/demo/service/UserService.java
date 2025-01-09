package com.example.demo.service;

import com.example.demo.database.entity.User;
import com.example.demo.model.user.LoginUser;
import com.example.demo.model.user.UserCreateDto;
import com.example.demo.model.user.UserListDto;
import com.example.demo.model.user.UserViewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void createUser(UserCreateDto request);

    List<UserViewDto> getAllUserDetails();

    Page<UserListDto> getAllUser(Pageable pageable);

    int getTotalPages(int pageSize);

    boolean isLoginNameExist(String loginName);

    User login(LoginUser loginUser);

    User logout(LoginUser loginUser);

    UserViewDto findByEmail(String email);
    Optional<User> findUserByEmail(String email);

    void updateUser(UserViewDto request);

    void save(UserViewDto userViewDto);

    User changeStatus(String email);

    Page<UserListDto> searchUser(String keyword, String role, Pageable pageable);
}
