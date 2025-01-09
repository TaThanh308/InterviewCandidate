package com.example.demo.controller;

import com.example.demo.database.entity.User;
import com.example.demo.database.repository.UserRepository;
import com.example.demo.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;
import java.util.UUID;

@Controller
public class PasswordController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailService emailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgotPass";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        if (!userOptional.isPresent()) {
            model.addAttribute("error", "Email is not valid.");
            return "forgotPass";
        }

        User user = userOptional.get();
        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        userRepository.save(user);

        emailService.sendResetPasswordEmail(email, token);

        model.addAttribute("message", "A password reset link has been sent to " + email);
        return "forgotPass";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        Optional<User> userOptional = userRepository.findByResetPasswordToken(token);
        if (!userOptional.isPresent()) {
            model.addAttribute("error", "Token is not valid.");
            return "error";
        }

        model.addAttribute("token", token);
        return "resetPass";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(
            @RequestParam("token") String token,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            model.addAttribute("token", token);
            return "resetPass";
        }

        Optional<User> userOptional = userRepository.findByResetPasswordToken(token);
        if (!userOptional.isPresent()) {
            model.addAttribute("error", "Token is not valid.");
            return "error";
        }

        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(password)); // Mã hóa mật khẩu trước khi lưu
        user.setResetPasswordToken(null); // Xóa token sau khi đặt lại mật khẩu thành công
        userRepository.save(user);

        model.addAttribute("message", "Password has been successfully reset.");
        return "resetPass";
    }
}

