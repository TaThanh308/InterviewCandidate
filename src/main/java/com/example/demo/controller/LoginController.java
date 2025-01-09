package com.example.demo.controller;

import com.example.demo.customError.PasswordMismatchException;
import com.example.demo.customError.UserNotValidException;
import com.example.demo.database.entity.User;
import com.example.demo.database.repository.UserRepository;
import com.example.demo.model.user.LoginUser;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class LoginController {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute("loginUser") LoginUser loginUser, BindingResult result,
                            HttpSession session) {
        try {
            User currentUser = userService.login(loginUser);
            session.setAttribute("currentUser", currentUser);
            return "redirect:/homepage";
        } catch (UserNotValidException e) {
            result.rejectValue("username", "error.loginUser", e.getMessage());
        } catch (PasswordMismatchException e) {
            result.rejectValue("password", "error.loginUser", e.getMessage());
        }
        return "login";
    }

}