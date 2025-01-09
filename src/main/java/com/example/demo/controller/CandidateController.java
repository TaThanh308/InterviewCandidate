package com.example.demo.controller;

import com.example.demo.customError.CustomExceptions.*;
import com.example.demo.customError.UserNotValidException;
import com.example.demo.database.entity.User;
import com.example.demo.database.repository.UserRepository;
import com.example.demo.model.candidate.CandidateDetailDto;
import com.example.demo.model.candidate.CandidatePostDto;
import com.example.demo.model.candidate.CandidateViewDto;
import com.example.demo.service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/candidate")
public class CandidateController {
    @Autowired
    private CandidateService candidateService;

    @Autowired
    private UserRepository userRepository;

    private final Integer pageSize = 3;

    @GetMapping
    public String getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String roleName = "";
        Optional<User> userOptional = userRepository.findUserByUsername(authentication.getName());
        if (userOptional.isEmpty()) {
            throw new UserNotValidException("User is not valid");
        }
        String url = "redirect:/candidate/listAll";

        if (authentication != null) {

            for (GrantedAuthority authority : authentication.getAuthorities()) {
                roleName = authority.getAuthority(); // Return the first role name
            }
        }
        if (roleName.equalsIgnoreCase("ROLE_INTERVIEWER")){
            return "redirect:/candidate/interviewer/" + userOptional.get().getUsername();
        } else if (roleName.equalsIgnoreCase("ROLE_RECRUITER")) {
            return "redirect:/candidate/recruiter/" + userOptional.get().getUsername();
        } else {
            return url;
        }
    }

    @GetMapping("/listAll")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String candidate(@RequestParam(defaultValue = "0") int page,
                            Model model, Principal principal) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<CandidateViewDto> candidatePage = candidateService.getAllCandidates(pageable);

        model.addAttribute("username", principal.getName());
        model.addAttribute("candidateList", candidatePage.getContent());
        model.addAttribute("currentPage", page + 1);
        model.addAttribute("totalPages", candidatePage.getTotalPages());
        model.addAttribute("totalElements", candidatePage.getTotalElements());
        model.addAttribute("keyword", null);
        model.addAttribute("status", null);

        return "candidate";
    }

    @GetMapping("/interviewer/{username}")
    @PreAuthorize("hasAnyRole('INTERVIEWER')")
    public String candidateInterviewer(@RequestParam(defaultValue = "0") int page, Model model,
                                       @PathVariable String username, Principal principal) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<CandidateViewDto> candidatePage =
                candidateService.getAllByInterviewer(pageable, username);

        model.addAttribute("username", principal.getName());
        model.addAttribute("candidateList", candidatePage.getContent());
        model.addAttribute("currentPage", page + 1);
        model.addAttribute("totalPages", candidatePage.getTotalPages());
        model.addAttribute("totalElements", candidatePage.getTotalElements());
        model.addAttribute("keyword", null);
        model.addAttribute("status", null);

        return "candidateForInter";
    }

    @GetMapping("/recruiter/{username}")
    @PreAuthorize("hasAnyRole('RECRUITER')")
    public String candidateRecruiter(@RequestParam(defaultValue = "0") int page, Model model,
                                       @PathVariable String username, Principal principal) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<CandidateViewDto> candidatePage =
                candidateService.getAllByRecruiter(pageable, username);

        model.addAttribute("username", principal.getName());
        model.addAttribute("candidateList", candidatePage.getContent());
        model.addAttribute("currentPage", page + 1);
        model.addAttribute("totalPages", candidatePage.getTotalPages());
        model.addAttribute("totalElements", candidatePage.getTotalElements());
        model.addAttribute("keyword", null);
        model.addAttribute("status", null);

        return "candidateForRecruiter";
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String searchCandidates(@RequestParam(value = "keyword", required = false) String keyword,
                                   @RequestParam(value = "status", required = false) String status,
                                   @RequestParam(defaultValue = "0") int page,
                                   Model model, Principal principal) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<CandidateViewDto> candidatePage = candidateService.searchCandidates(keyword,
                status, pageable);

        model.addAttribute("username", principal.getName());
        model.addAttribute("candidateList", candidatePage.getContent());
        model.addAttribute("currentPage", page + 1);
        model.addAttribute("totalPages", candidatePage.getTotalPages());
        model.addAttribute("totalElements", candidatePage.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);

        return "candidate";
    }

    @GetMapping("/searchByRecruiter")
    @PreAuthorize("hasAnyRole('RECRUITER')")
    public String searchCandidatesByRecruiter(@RequestParam(value = "keyword", required = false) String keyword,
                                              @RequestParam(value = "status", required = false) String status,
                                              @RequestParam(defaultValue = "0") int page,
                                              Model model, Principal principal) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<CandidateViewDto> candidatePage = candidateService.searchCandidatesByRecruiter(keyword,
                status, principal.getName(), pageable);

        model.addAttribute("username", principal.getName());
        model.addAttribute("candidateList", candidatePage.getContent());
        model.addAttribute("currentPage", page + 1);
        model.addAttribute("totalPages", candidatePage.getTotalPages());
        model.addAttribute("totalElements", candidatePage.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);

        return "candidateForRecruiter";
    }

    @GetMapping("/searchByInterviewer")
    @PreAuthorize("hasAnyRole('INTERVIEWER')")
    public String searchCandidatesByInterviewer(@RequestParam(value = "keyword", required = false) String keyword,
                                                @RequestParam(value = "status", required = false) String status,
                                                @RequestParam(defaultValue = "0") int page,
                                                Model model, Principal principal) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<CandidateViewDto> candidatePage = candidateService.searchCandidatesByInterviewer(keyword,
                status, principal.getName(), pageable);

        model.addAttribute("username", principal.getName());
        model.addAttribute("candidateList", candidatePage.getContent());
        model.addAttribute("currentPage", page + 1);
        model.addAttribute("totalPages", candidatePage.getTotalPages());
        model.addAttribute("totalElements", candidatePage.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);

        return "candidateForInter";
    }

    @GetMapping("/form/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'RECRUITER')")
    public String candidateCreateForm(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("candidatePostDto", new CandidatePostDto());

        Optional<User> user = userRepository.findUserByUsername(principal.getName());
        if (user.isEmpty()) {
            throw new UserNotValidException("User is not valid");
        }
        if (user.get().getRole().equals("Recruiter")) {
            List<User> recruiterList = List.of(user.get());
            model.addAttribute("recruiterList", recruiterList);
        } else {
            model.addAttribute("recruiterList", candidateService.getAllRecruiters());
        }

        return "candidateCreate";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'RECRUITER')")
    public String createCandidate(@ModelAttribute CandidatePostDto request, BindingResult result,
                                  RedirectAttributes redirectAttributes, Model model, Principal principal) {
        try {
            candidateService.createCandidate(request);
            int totalPage = candidateService.getTotalPages(pageSize);
            redirectAttributes.addFlashAttribute("success",
                    "Candidate created successfully");
            return "redirect:/candidate?page=" + (totalPage - 1);
        } catch (NameRequiredException e) {
            result.rejectValue("name", "error.candidatePostDto", e.getMessage());
        } catch (EmailRequiredException e) {
            result.rejectValue("email", "error.candidatePostDto", e.getMessage());
        } catch (GenderRequiredException e) {
            result.rejectValue("gender", "error.candidatePostDto", e.getMessage());
        } catch (PositionRequiredException e) {
            result.rejectValue("position", "error.candidatePostDto", e.getMessage());
        } catch (SkillsRequiredException e) {
            result.rejectValue("skills", "error.candidatePostDto", e.getMessage());
        } catch (RecruiterRequiredException e) {
            result.rejectValue("recruiter", "error.candidatePostDto", e.getMessage());
        } catch (StatusRequiredException e) {
            result.rejectValue("status", "error.candidatePostDto", e.getMessage());
        } catch (HighestLevelRequiredException e) {
            result.rejectValue("highestLevel", "error.candidatePostDto", e.getMessage());
        } catch (DateOfBirthException e) {
            result.rejectValue("dob", "error.candidatePostDto", e.getMessage());
        } catch (PhoneRequiredException e) {
            result.rejectValue("phone", "error.candidatePostDto", e.getMessage());
        }
        model.addAttribute("username", principal.getName());
        Optional<User> user = userRepository.findUserByUsername(principal.getName());
        if (user.isEmpty()) {
            throw new UserNotValidException("User is not valid");
        }
        if (user.get().getRole().equals("Recruiter")) {
            List<User> recruiterList = List.of(user.get());
            model.addAttribute("recruiterList", recruiterList);
        } else {
            model.addAttribute("recruiterList", candidateService.getAllRecruiters());
        }
        return "candidateCreate";
    }

    @GetMapping("/detail/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'RECRUITER')")
    public String candidateDetail(@PathVariable Integer id, Model model, Principal principal) {
        Optional<User> user = userRepository.findUserByUsername(principal.getName());
        if (user.isEmpty()) {
            throw new UserNotValidException("User is not valid");
        }
        if (user.get().getRole().equals("Recruiter") &&
                !candidateService.isCandidateRecruitedBy(id, principal.getName())) {
            return "redirect:/404";
        }
        model.addAttribute("username", principal.getName());
        CandidateDetailDto candidate = candidateService.findById(id);
        model.addAttribute("candidate", candidate);
        return "candidateDetail";
    }

    @GetMapping("/interviewer/detail/{id}")
    @PreAuthorize("hasAnyRole('INTERVIEWER')")
    public String candidateDetailInterviewer(@PathVariable Integer id, Model model, Principal principal) {
        if (!candidateService.isCandidateInterviewedBy(id, principal.getName())) {
            return "redirect:/404";
        }
        model.addAttribute("username", principal.getName());
        CandidateDetailDto candidate = candidateService.findById(id);
        model.addAttribute("candidate", candidate);
        return "candidateDetailForInter";
    }

    @PostMapping("/delete/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'RECRUITER')")
    public String deleteCandidate(@PathVariable String email, RedirectAttributes redirectAttributes) {
        try {
            candidateService.deleteCandidate(email);
            redirectAttributes.addFlashAttribute("success",
                    "Candidate deleted successfully");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error",
                    e.getMessage());
        }
        return "redirect:/candidate";
    }

    @PostMapping("/ban/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'RECRUITER')")
    public String banCandidate(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            candidateService.banCandidate(id);
            redirectAttributes.addFlashAttribute("success",
                    "Candidate banned successfully");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error",
                    e.getMessage());
        }
        return "redirect:/candidate/detail/" + id;
    }

    @GetMapping("/form/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'RECRUITER')")
    public String candidateUpdate(@PathVariable Integer id, Model model, Principal principal,
                                  RedirectAttributes redirectAttributes) {
        Optional<User> user = userRepository.findUserByUsername(principal.getName());
        if (user.isEmpty()) {
            throw new UserNotValidException("User is not valid");
        }
        if (user.get().getRole().equals("Recruiter") &&
                !candidateService.isCandidateRecruitedBy(id, principal.getName())) {
            return "redirect:/404";
        }
        CandidatePostDto candidate = candidateService.findCandidateById(id);
        if (candidate.getStatus().equals("Banned")) {
            redirectAttributes.addFlashAttribute("error",
                    "Cannot update a banned candidate");
            return "redirect:/candidate/detail/" + candidate.getId();
        }
        model.addAttribute("username", principal.getName());
        model.addAttribute("candidatePostDto", candidate);
        if (user.get().getRole().equals("Recruiter")) {
            List<User> recruiterList = List.of(user.get());
            model.addAttribute("recruiterList", recruiterList);
        } else {
            model.addAttribute("recruiterList", candidateService.getAllRecruiters());
        }
        return "candidateEdit";
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'RECRUITER')")
    public String updateCandidate(@ModelAttribute CandidatePostDto request, BindingResult result,
                                RedirectAttributes redirectAttributes, Model model, Principal principal) {
        try {
            candidateService.updateCandidate(request);
            redirectAttributes.addFlashAttribute("success",
                    "Candidate updated successfully");
            return "redirect:/candidate/detail/" + request.getId();
        } catch (NameRequiredException e) {
            result.rejectValue("name", "error.candidatePostDto", e.getMessage());
        } catch (EmailRequiredException e) {
            result.rejectValue("email", "error.candidatePostDto", e.getMessage());
        } catch (DateOfBirthException e) {
            result.rejectValue("dob", "error.candidatePostDto", e.getMessage());
        } catch (PhoneRequiredException e) {
            result.rejectValue("phone", "error.candidatePostDto", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    e.getMessage());
            return "redirect:/candidate/detail/" + request.getId();
        }
        model.addAttribute("username", principal.getName());
        Optional<User> user = userRepository.findUserByUsername(principal.getName());
        if (user.isEmpty()) {
            throw new UserNotValidException("User is not valid");
        }
        if (user.get().getRole().equals("Recruiter")) {
            List<User> recruiterList = List.of(user.get());
            model.addAttribute("recruiterList", recruiterList);
        } else {
            model.addAttribute("recruiterList", candidateService.getAllRecruiters());
        }
        return "candidateEdit";
    }
}
