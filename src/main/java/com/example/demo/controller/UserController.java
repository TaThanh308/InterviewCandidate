package com.example.demo.controller;

import com.example.demo.customError.UserNotValidException;
import com.example.demo.database.entity.User;
import com.example.demo.database.repository.UserRepository;
import com.example.demo.model.user.UserCreateDto;
import com.example.demo.model.user.UserListDto;
import com.example.demo.model.user.UserViewDto;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private final Integer pageSize = 2;

    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam String username) {
        boolean isUnique = !userRepository.existsByUsername(username);
        Map<String, Boolean> response = new HashMap<>();
        response.put("unique", isUnique);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public String getUser(Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String roleName = "";
        Optional<User> userOptional = userRepository.findUserByUsername(authentication.getName());
        if (userOptional.isEmpty()) {
            throw new UserNotValidException("User is not valid");
        }
        String url = "redirect:/user/userDetail/" + userOptional.get().getEmail();

        if (authentication != null) {

            for (GrantedAuthority authority : authentication.getAuthorities()) {
                roleName = authority.getAuthority(); // Return the first role name
            }
        }
        if(roleName.equalsIgnoreCase("ROLE_ADMIN")){
            return "redirect:/user/adminList";
        } else {
            return url;
        }
    }

    @GetMapping("/search")
    public String searchUser(@RequestParam(value = "keyword", required = false) String keyword,
                             @RequestParam(value = "role", required = false) String role,
                             @RequestParam(defaultValue = "0") int page,
                             Model model, Principal principal) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<UserListDto> userPage = userService.searchUser(keyword, role, pageable);

        model.addAttribute("username", principal.getName());
        model.addAttribute("userlist", userPage.getContent());
        model.addAttribute("currentPage", page + 1);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalElements", userPage.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("role", role);

        return "adminUser";
    }

    @GetMapping("/adminList")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String adminUser(@RequestParam(defaultValue = "0") int page, Model model, Principal principal) {
        System.out.println(principal);
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<UserListDto> userPage = userService.getAllUser(pageable);
        model.addAttribute("username", principal.getName());
        model.addAttribute("userlist", userPage.getContent());
        model.addAttribute("currentPage", page + 1);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalElements", userPage.getTotalElements());
        return "adminUser";
    }

//    @GetMapping("/userList/{email}")
//    @PreAuthorize("hasAnyRole('RECRUITER', 'INTERVIEWER', 'MANAGER')")
//    public String showUser(@PathVariable String email, Model model, Principal principal) {
//        UserViewDto userViewDto = userService.findByEmail(email);
//        if (userViewDto == null) {
//            throw new RuntimeException("User not found");
//        }
//        model.addAttribute("username", principal.getName());
//        model.addAttribute("user", userViewDto);
//        return "user";
//    }

    @GetMapping("/form/create")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String userCreateForm(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("userCreateDto", new UserCreateDto());
        return "userCreate";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String createUser(@ModelAttribute UserCreateDto userCreateDto, Model model, Principal principal) {
        try {
            userService.createUser(userCreateDto);
            int totalPage = userService.getTotalPages(pageSize);
            return "redirect:/user/adminList?page=" + (totalPage - 1);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("username", principal.getName());
        model.addAttribute("userCreateDto", new UserCreateDto());
        return "adminList";
    }

    @GetMapping("/adminDetail/{email}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String adminDetail(@PathVariable String email, Model model, Principal principal) {
        UserViewDto userViewDto = userService.findByEmail(email);
        if (userViewDto == null) {
            throw new RuntimeException("User not found");
        }
        model.addAttribute("username", principal.getName());
        model.addAttribute("user", userViewDto);
        return "userDetailForAdmin";
    }

    @GetMapping("/userDetail/{email}")
    @PreAuthorize("hasAnyRole('RECRUITER', 'INTERVIEWER', 'MANAGER')")
    public String userDetail(@PathVariable String email, Model model, Principal principal) {
        UserViewDto userViewDto = userService.findByEmail(email);
        if (userViewDto == null) {
            throw new RuntimeException("User not found");
        }
        model.addAttribute("username", principal.getName());
        model.addAttribute("userView", userViewDto);
        return "userDetailForUser";
    }

    @PostMapping("/changeStatus/{email}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String changeUserStatus(@PathVariable String email, Model model, Principal principal) {
        User user = userService.changeStatus(email);
        model.addAttribute("username", principal.getName());
        model.addAttribute("user", user);
        return "userDetailForAdmin";
    }


    @GetMapping("/adminUpdate/{email}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String showForm(@PathVariable String email, Model model, Principal principal) {
        UserViewDto userViewDto = userService.findByEmail(email);
        model.addAttribute("username", principal.getName());
        model.addAttribute("adminUpdate", userViewDto);
        return "userEditForAdmin";
    }

    @PostMapping("/adminUpdate")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String adminUpdate(@ModelAttribute UserViewDto userViewDto, BindingResult result) {
        if (result.hasErrors()) {
            return "userEditForAdmin";
        }
        userService.updateUser(userViewDto);
        return "redirect:/user/adminDetail/" + userViewDto.getEmail();
    }

    @GetMapping("/userUpdate/{email}")
    @PreAuthorize("hasAnyRole('RECRUITER', 'INTERVIEWER', 'MANAGER')")
    public String showEditForm(@PathVariable String email, Model model, Principal principal) {
        UserViewDto userViewDto = userService.findByEmail(email);
        model.addAttribute("username", principal.getName());
        model.addAttribute("userUpdate", userViewDto);
        return "userEditForUser";
    }

    @PostMapping("/userUpdate")
    @PreAuthorize("hasAnyRole('RECRUITER', 'INTERVIEWER', 'MANAGER')")
    public String updateUser(@ModelAttribute UserViewDto userViewDto, BindingResult result) {
        if (result.hasErrors()) {
            return "userEditForUser";
        }
        userService.updateUser(userViewDto);
        return "redirect:/user/userDetail/" + userViewDto.getEmail();
    }
}

