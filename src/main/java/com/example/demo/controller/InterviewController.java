package com.example.demo.controller;

import com.example.demo.customError.InterviewScheduleValidator;
import com.example.demo.customError.InterviewValidator;
import com.example.demo.customError.UserNotValidException;
import com.example.demo.database.entity.Candidate;
import com.example.demo.database.entity.Job;
import com.example.demo.database.entity.User;
import com.example.demo.database.repository.UserRepository;
import com.example.demo.model.interview.InterviewCandidate;
import com.example.demo.model.interview.InterviewEditDto;
import com.example.demo.model.interview.InterviewPageDto;
import com.example.demo.model.interview.InterviewPostDto;
import com.example.demo.service.InterviewService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Log4j2
@Controller
@RequestMapping("/interview")
public class InterviewController {
    @Autowired
    private InterviewService interviewService;

    @Autowired
    private InterviewValidator interviewValidator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InterviewScheduleValidator interviewScheduleValidator;

    @GetMapping
    public String getInterview(Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String roleName = "";
        Optional<User> userOptional = userRepository.findUserByUsername(authentication.getName());
        if (userOptional.isEmpty()) {
            throw new UserNotValidException("User is not valid");
        }
        String url = "redirect:/interview/showListData" ;

        if (authentication != null) {

            for (GrantedAuthority authority : authentication.getAuthorities()) {
                roleName = authority.getAuthority(); // Return the first role name
            }
        }
        if(roleName.equalsIgnoreCase("ROLE_INTERVIEWER")){
            return "redirect:/interview/interviewer/" + userOptional.get().getUsername();
        } else {
            return url;
        }
    }

    // manager and recruiter
    @GetMapping("/showCreate")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER', 'MANAGER')")
    public String showAddInterview(Model model , Principal principal) {

        InterviewPostDto interviewPostDto = new InterviewPostDto();
        List<Job> jobList = interviewService.getAllJob();
        List<Candidate> candidateList = interviewService.getAllCandidate();
        List<InterviewCandidate> interviewCandidates = candidateList.stream()
                .map(c -> new InterviewCandidate(c.getCandidateId(), c.getName(),c.getEmail()))
                .collect(Collectors.toList());

        interviewPostDto.setCandidates(interviewCandidates);

        List<User> userList = interviewService.getAllUser();
        List<User> userInterviewerList = interviewService.getAllUserInterviewer();
        model.addAttribute("interviewPostDto", interviewPostDto);
        model.addAttribute("jobsList", jobList);
        model.addAttribute("candidateList", candidateList);
        model.addAttribute("userList", userList);
        model.addAttribute("userInterviewerList", userInterviewerList);
        model.addAttribute("username", principal.getName());

        return "interviewCreate";
    }

    @PostMapping("/createInterview")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER', 'MANAGER')")
    public String createInterview(@ModelAttribute("interviewPostDto") InterviewPostDto interviewPostDto, BindingResult bindingResult, Model model){
        interviewValidator.validate(interviewPostDto, bindingResult);
        if (bindingResult.hasErrors()) {
            List<Job> jobList = interviewService.getAllJob();
            List<Candidate> candidateList = interviewService.getAllCandidate();
            List<InterviewCandidate> interviewCandidates = candidateList.stream()
                    .map(c -> new InterviewCandidate(c.getCandidateId(), c.getName(),c.getEmail()))
                    .collect(Collectors.toList());
            interviewPostDto.setCandidates(interviewCandidates);
            List<User> userList = interviewService.getAllUser();
            List<User> userInterviewerList = interviewService.getAllUserInterviewer();

            model.addAttribute("jobsList", jobList);
            model.addAttribute("candidateList", candidateList);
            model.addAttribute("userList", userList);
            model.addAttribute("userInterviewerList", userInterviewerList);

            return "interviewCreate";
        }
        interviewService.createInterview(interviewPostDto);
        return "redirect:/interview";
    }


    @GetMapping("/showListData")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','MANAGER')")
    public String getInterviews(Model model,  InterviewPageDto interviewPageDto, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size , @RequestParam(required = false) String keyword,
                                @RequestParam(required = false) String status,
                                Principal principal) {
        if ((keyword != null && !keyword.isEmpty()) || (status != null && !status.isEmpty())) {
             interviewPageDto = interviewService.searchInterviewr(keyword, status, page, size);
        } else {
             interviewPageDto = interviewService.pageInterview(page, size);
        }
        model.addAttribute("interviewLists", interviewPageDto.getInterviewViewDtoList());
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage", interviewPageDto.getTotalPage());
        model.addAttribute("totalElement", interviewPageDto.getTotalElements());
        model.addAttribute("username", principal.getName());
        return "interview";
    }





    @GetMapping("/ViewInterview/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER', 'MANAGER')")
    public String getInterviewDetail(@PathVariable Integer id, Model model , Principal principal) {
        InterviewPageDto.InterviewViewDto viewInterviewDto = interviewService.getInterviewById(id);
        model.addAttribute("viewInterviewDto", viewInterviewDto);
        model.addAttribute("username", principal.getName());
        return "interviewView";
    }

    @GetMapping("/showEdit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER', 'MANAGER')")
    public String showEdit(@PathVariable(value = "id")  Integer id, Model model , Principal principal) {
        Optional<User> user = userRepository.findUserByUsername(principal.getName());
        if (user.isEmpty()) {
            throw new UserNotValidException("User is not valid");
        }
        if (user.get().getRole().equals("Recruiter") &&
                !interviewService.isInterviewRecruitedBy(id, principal.getName())) {
            return "redirect:/404";
        }
        InterviewEditDto edit = interviewService.findById(id);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = edit.getScheduleTime().format(formatter);
        model.addAttribute("formattedScheduleTime", formattedDate);
        model.addAttribute("edit", edit);
        List<Job> jobList = interviewService.getAllJob();
        model.addAttribute("jobsList", jobList);
        List<Candidate> candidateList = interviewService.getAllCandidateUser();
        model.addAttribute("candidateList", candidateList);
        List<User> userList = interviewService.getAllUser();
        model.addAttribute("userList", userList);
        List<User> userInterviewerList = interviewService.getAllUserInterviewer();
        model.addAttribute("userInterviewerList", userInterviewerList);
        model.addAttribute("username", principal.getName());
        List<String> selectedInterviewers = Arrays.asList(edit.getInterview().split(","));
        model.addAttribute("selectedInterviewers", selectedInterviewers);

        return "interviewEdit";
    }
    @PostMapping("/edit")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER', 'MANAGER')")
    public String edit(@ModelAttribute("edit") InterviewEditDto interviewPostDto, BindingResult bindingResult, Model model , Principal principal)  throws Exception {
        interviewScheduleValidator.validate(interviewPostDto, bindingResult);
        if (bindingResult.hasErrors()) {
            List<Job> jobList = interviewService.getAllJob();
            model.addAttribute("jobsList", jobList);
            List<User> userList = interviewService.getAllUser();
            model.addAttribute("userList", userList);
            List<User> userInterviewerList = interviewService.getAllUserInterviewer();
            model.addAttribute("userInterviewerList", userInterviewerList);
            model.addAttribute("username", principal.getName());
            return "interviewEdit";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        interviewPostDto.setScheduleTime(LocalDate.parse(interviewPostDto.getScheduleTime().format((formatter))));
        interviewService.EditInterview(interviewPostDto, interviewPostDto.getId());
        return "redirect:/interview/ViewInterview/" + interviewPostDto.getId();
    }

    @PostMapping("/cancelSchedule")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER', 'MANAGER')")
    public String cancelSchedule(@RequestParam("id")  Integer id , Principal principal) {
        Optional<User> user = userRepository.findUserByUsername(principal.getName());
        if (user.isEmpty()) {
            throw new UserNotValidException("User is not valid");
        }
        if (user.get().getRole().equals("Recruiter") &&
                !interviewService.isInterviewRecruitedBy(id, principal.getName())) {
            return "redirect:/404";
        }
        interviewService.cancelSchedule(id);
        return "redirect:/interview";
    }

    @PostMapping("/sendEmail")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER', 'MANAGER')")
    public String sendEmail(@RequestParam("interviewId") Integer interviewId, RedirectAttributes redirectAttributes , Principal principal) {
        Optional<User> user = userRepository.findUserByUsername(principal.getName());
        if (user.isEmpty()) {
            throw new UserNotValidException("User is not valid");
        }
        if (user.get().getRole().equals("Recruiter") &&
                !interviewService.isInterviewRecruitedBy(interviewId, principal.getName())) {
            return "redirect:/404";
        }
        try {
            InterviewEditDto interviewPostDto = interviewService.findById(interviewId);
            if (!"New".equals(interviewPostDto.getStatus())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Only interviews with status 'New' can be sent via email.");
                return "redirect:/interview/ViewInterview/"+interviewId;
            }
            interviewService.sendInterviewDetailsToEmail(interviewId);
            redirectAttributes.addFlashAttribute("successMessage", "Email sent successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to send email: " + e.getMessage());
        }
        return "redirect:/interview";
    }

    //Interviewer
    @GetMapping("/interviewer/{username}")
    @PreAuthorize("hasAnyRole('INTERVIEWER')")
    public String getInterviewers(Model model, InterviewPageDto interviewPageDto,
                                  @RequestParam(defaultValue = "0") Integer page,
                                  @RequestParam(defaultValue = "1") Integer size,
                                  @RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) String status,
                                  @PathVariable String username ,
                                    Principal principal) {

        if ((keyword != null && !keyword.isEmpty()) || (status != null && !status.isEmpty())) {
            interviewPageDto = interviewService.searchInterviewByUsername(keyword, status, page, size, username );
        } else {
            interviewPageDto = interviewService.pageInterviewByUsername(page, size, username );
        }
        model.addAttribute("interviewerLists", interviewPageDto.getInterviewViewDtoList());
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPage", interviewPageDto.getTotalPage());
        model.addAttribute("totalElement", interviewPageDto.getTotalElements());
        model.addAttribute("username", principal.getName());
        return "interviewer";
    }


    @GetMapping("/interviewer/ViewInterviewer/{id}")
    @PreAuthorize("hasAnyRole('INTERVIEWER')")
    public String getInterviewDetail1(@PathVariable Integer id, Model model , Principal principal) {
        if (!interviewService.isInterviewInterviewedBy(id, principal.getName())) {
            return "redirect:/404";
        }
        InterviewPageDto.InterviewViewDto viewInterviewerDto = interviewService.getInterviewById(id);
        model.addAttribute("viewInterviewer", viewInterviewerDto);
        model.addAttribute("username", principal.getName());
        return "interviewerView";
    }

    @GetMapping("/interviewer/showSubmit/{id}")
    @PreAuthorize("hasAnyRole('INTERVIEWER')")
    public String showSubmit(@PathVariable(value = "id")  Integer id, Model model , Principal principal) {
        if (!interviewService.isInterviewInterviewedBy(id, principal.getName())) {
            return "redirect:/404";
        }
        InterviewEditDto submit = interviewService.findById(id);
        model.addAttribute("submit",submit);
        List<Job> jobList = interviewService.getAllJob();
        model.addAttribute("jobsList", jobList);
        List<Candidate> candidateList = interviewService.getAllCandidate();
        model.addAttribute("candidateList", candidateList);
        List<User> userList = interviewService.getAllUser();
        model.addAttribute("userList", userList);
        List<User> userInterviewerList = interviewService.getAllUserInterviewer();
        model.addAttribute("userInterviewerList", userInterviewerList);
        model.addAttribute("username", principal.getName());
        return "interviewerSubmit";
    }
    @PostMapping("/interviewer/submitEdit")
    @PreAuthorize("hasAnyRole('INTERVIEWER')")
    public String submit(@ModelAttribute InterviewEditDto interviewPostDto, BindingResult bindingResult, Model model, Principal principal)  throws Exception {
        if(bindingResult.hasErrors()) {
            List<Job> jobList = interviewService.getAllJob();
            model.addAttribute("jobsList", jobList);
            List<Candidate> candidateList = interviewService.getAllCandidate();
            model.addAttribute("candidateList", candidateList);
            List<User> userList = interviewService.getAllUser();
            model.addAttribute("userList", userList);
            List<User> userInterviewerList = interviewService.getAllUserInterviewer();
            model.addAttribute("userInterviewerList", userInterviewerList);
            model.addAttribute("username", principal.getName());
            return "interviewerSubmit";
        }
        InterviewPostDto interviewPostDto1 = new InterviewPostDto();
        interviewPostDto1.setId(interviewPostDto.getId());
        interviewPostDto1.setNotes(interviewPostDto.getNotes());
        interviewPostDto1.setResult(interviewPostDto.getResult());
        interviewPostDto1.setStatus(interviewPostDto.getStatus());
        interviewService.Interviewed(interviewPostDto1, interviewPostDto.getId());
        return "redirect:/interview/interviewer/"  + principal.getName();
    }

}




