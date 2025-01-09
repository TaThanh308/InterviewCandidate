package com.example.demo.service.impl;

import com.example.demo.customError.CustomExceptions;
import com.example.demo.customError.PasswordMismatchException;
import com.example.demo.customError.UserNotValidException;
import com.example.demo.database.entity.User;
import com.example.demo.database.repository.UserRepository;
import com.example.demo.model.user.*;
import com.example.demo.service.MailService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailService mailService;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean isLoginNameExist(String username) {
        Optional<User> userOptional = userRepository.findUserByUsername(username);
        return userOptional.isPresent();
    }

    @Override
    public User login(LoginUser loginUser) {
        Optional<User> userOptional = userRepository.findUserByUsername(loginUser.getUsername());
        if (userOptional.isEmpty()) {
            throw new UserNotValidException("User is not valid");
        } else if (!loginUser.getPassword().equals(userOptional.get().getPassword())) {
            throw new PasswordMismatchException("Passwords do not match");
        }
        User currentUser = new User();
        currentUser.setUsername(userOptional.get().getUsername());
        currentUser.setEmail(userOptional.get().getEmail());
        currentUser.setName(userOptional.get().getName());
        currentUser.setDob(userOptional.get().getDob());
        currentUser.setAddress(userOptional.get().getAddress());
        currentUser.setPhone(userOptional.get().getPhone());
        currentUser.setGender(userOptional.get().getGender());
        currentUser.setRole(userOptional.get().getRole());
        currentUser.setDepartment(userOptional.get().getDepartment());
        currentUser.setStatus(userOptional.get().getStatus());
        currentUser.setNotes(userOptional.get().getNotes());
        currentUser.setCreatedAt(userOptional.get().getCreatedAt());
        return currentUser;
    }

    @Override
    public User logout(LoginUser loginUser) {
        return null;
    }

    @Override
    public Page<UserListDto> getAllUser(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        List<UserListDto> userListDtoList = userPage.stream().map(user -> {
            UserListDto userListDto = new UserListDto();
            userListDto.setId(user.getUserId());
            userListDto.setUsername(user.getUsername());
            userListDto.setEmail(user.getEmail());
            userListDto.setPhone(user.getPhone());
            userListDto.setRole(user.getRole());
            userListDto.setStatus(user.getStatus());
            return userListDto;
        }).collect(Collectors.toList());

        return new PageImpl<>(userListDtoList, pageable, userPage.getTotalElements());
    }

    @Override
    public void createUser(UserCreateDto request) {
        Optional<User> userOptional = userRepository.findUserByEmail(request.getEmail());
        if (userOptional.isPresent()) {
            throw new CustomExceptions.EmailRequiredException("Email already exists");
        }

        User createUser = new User();
        createUser.setUsername(request.getUsername());
        createUser.setName(request.getName());
        createUser.setEmail(request.getEmail());
        createUser.setDob(request.getDob());
        createUser.setAddress(request.getAddress());
        createUser.setPhone(request.getPhone());
        createUser.setGender(request.getGender());
        createUser.setRole(request.getRole());
        createUser.setDepartment(request.getDepartment());
        createUser.setStatus(request.getStatus());
        createUser.setNotes(request.getNotes());
        createUser.setCreatedAt(LocalDateTime.now());
        createUser.setUpdatedAt(LocalDateTime.now());
        createUser.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(createUser);
        mailService.sendMail(request.getEmail(), "no-reply-email-IMS-system <Account created>",
                "This email is from IMS system\n"
                        + "Your account has been created. Please use the following credential to login:\n"
                        + "User name:" + request.getUsername()
                        + "\nPassword:" + request.getPassword()
                        + "\nIf anything wrong, please reach-out recruiter < offer recruiter owner account>. We are so sorry for this inconvenience\n"
                        + "Thanks & Regards!\n"
                        + "IMS Team.”");
    }

    @Override
    public List<UserViewDto> getAllUserDetails() {
        List<User> users = userRepository.findAll();
        List<UserViewDto> userViewList = new ArrayList<>();
        for (User users1 : users) {
            UserViewDto userViewDto = new UserViewDto();
            userViewDto.setName(users1.getName());
            userViewDto.setEmail(users1.getEmail());
            userViewDto.setDob(users1.getDob());
            userViewDto.setAddress(users1.getAddress());
            userViewDto.setPhone(users1.getPhone());
            userViewDto.setGender(users1.getGender());
            userViewDto.setRole(users1.getRole());
            userViewDto.setDepartment(users1.getDepartment());
            userViewDto.setStatus(users1.getStatus());
            userViewDto.setNotes(users1.getNotes());
            userViewList.add(userViewDto);
        }
        return userViewList;
    }



    @Override
    public UserViewDto findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        } else {
            UserViewDto dto = new UserViewDto();
            dto.setName(user.getName());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setDob(user.getDob());
            dto.setAddress(user.getAddress());
            dto.setPhone(user.getPhone());
            dto.setGender(user.getGender());
            dto.setRole(user.getRole());
            dto.setDepartment(user.getDepartment());
            dto.setStatus(user.getStatus());
            dto.setNotes(user.getNotes());
            return dto;
        }
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return Optional.empty();
    }


    @Override
    public int getTotalPages(int pageSize) {
        return (int) Math.ceil((double) userRepository.count() / pageSize);
    }

    @Override
    public void updateUser(UserViewDto request) {
        Optional<User> optionalUser = userRepository.findUserByEmail(request.getEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setName(request.getName());
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setDob(request.getDob());
            user.setAddress(request.getAddress());
            user.setPhone(request.getPhone());
            user.setGender(request.getGender());
            user.setRole(request.getRole());
            user.setDepartment(request.getDepartment());
            user.setStatus(request.getStatus());
            user.setNotes(request.getNotes());
            userRepository.save(user);
        }
    }

    @Override
    public void save(UserViewDto userViewDto) {

    }

    @Override
    public User changeStatus(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            if ("Active".equals(user.getStatus())) {
                user.setStatus("Inactive");
            } else {
                user.setStatus("Active");
            }
            userRepository.save(user);
        }
        return user;
    }

    @Override
    public Page<UserListDto> searchUser(String keyword, String role, Pageable pageable) {
        Page<User> candidatePage = userRepository.searchByKeywordAndRole(keyword, role, pageable);
        List<UserListDto> candidateViewList = candidatePage.stream().map(this::convertToDto).collect(Collectors.toList());

        return new PageImpl<>(candidateViewList, pageable, candidatePage.getTotalElements());
    }

    private UserListDto convertToDto(User user) {
        UserListDto userListDto = new UserListDto();
        userListDto.setId(user.getUserId());
        userListDto.setUsername(user.getUsername());
        userListDto.setEmail(user.getEmail());
        userListDto.setPhone(user.getPhone());
        userListDto.setRole(user.getRole());
        userListDto.setStatus(user.getStatus());
        return userListDto;
    }


}

