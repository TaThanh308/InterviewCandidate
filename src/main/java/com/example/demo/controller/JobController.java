package com.example.demo.controller;

import com.example.demo.customError.UserNotValidException;
import com.example.demo.database.entity.User;
import com.example.demo.database.repository.UserRepository;
import com.example.demo.model.job.JobPostDto;
import com.example.demo.model.job.JobValidateProcessor;
import com.example.demo.model.job.JobViewPageForInterDto;
import com.example.demo.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/job")
public class JobController {

    @Autowired
    private JobService jobService;

    @Autowired
    private JobValidateProcessor jobValidateProcessor;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String getJob(Principal principal, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UserNotValidException("User is not valid"));

        model.addAttribute("username", principal.getName());

        String roleName = authentication.getAuthorities().stream()
                .findFirst()
                .map(Object::toString)
                .orElse("");

        if ("ROLE_INTERVIEWER".equalsIgnoreCase(roleName)) {
            return "redirect:/job/interviewer";
        } else {
            return "redirect:/job/joblist";
        }
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyRole('RECRUITER','MANAGER','ADMIN')")
    public String addJob(Model model, Principal principal) {
        model.addAttribute("crJob", new JobPostDto());
        model.addAttribute("username", principal.getName());
        return "jobCreate";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('RECRUITER','MANAGER','ADMIN')")
    public String createJob(@Validated @ModelAttribute("crJob") JobPostDto dto, BindingResult result,
                            Model model, Principal principal) {
        if (result.hasErrors()) {
            model.addAttribute("crJob", dto);
            model.addAttribute("username", principal.getName());
            return "jobCreate";
        }

        User currentUser = userRepository.findUserByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        jobValidateProcessor.validate(result, dto);
        if (result.hasErrors()) {
            model.addAttribute("crJob", dto);
            model.addAttribute("username", principal.getName());
            return "jobCreate";
        }

        jobService.createJob(dto, currentUser.getUserId());
        return "redirect:/job";
    }

    @GetMapping("/edit/{jobId}")
    @PreAuthorize("hasAnyRole('RECRUITER','MANAGER','ADMIN')")
    public String jobEditForm(@PathVariable Integer jobId, Model model, Principal principal) {
        User currentUser = userRepository.findUserByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        JobPostDto jobPostDto = jobService.findById(jobId);
        if (jobPostDto == null || jobPostDto.getCreatedBy() == null || !jobPostDto.getCreatedBy().equals(currentUser.getUserId())) {
            model.addAttribute("error", "You are not authorized to edit this job.");
            return "jobForRM"; // Trả về trang thông báo khi không có quyền sửa
        }

        List<String> availableSkills = Arrays.asList("Java", "Nodejs", ".Net", "C++", "Business analysis", "Communication");
        List<String> availableBenefits = Arrays.asList("Lunch", "25-day leave", "Healthcare insurance", "Hybrid working", "Travel");
        List<String> availableLevels = Arrays.asList("Fresher", "Junior", "Senior", "Leader", "Manager", "Vice Head");

        model.addAttribute("job", jobPostDto);
        model.addAttribute("availableSkills", availableSkills);
        model.addAttribute("availableBenefits", availableBenefits);
        model.addAttribute("availableLevels", availableLevels);
        model.addAttribute("username", principal.getName());

        return "jobEdit"; // Trả về form sửa công việc
    }

    // Phương thức POST để xử lý việc cập nhật công việc
    @PostMapping("/edit/{jobId}")
    @PreAuthorize("hasAnyRole('RECRUITER','MANAGER','ADMIN')")
    public String updateJob(@PathVariable Integer jobId, @Validated @ModelAttribute("job") JobPostDto dto,
                            BindingResult result, Model model, Principal principal) {
        User currentUser = userRepository.findUserByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        JobPostDto existingJob = jobService.findById(jobId);
        if (existingJob == null || existingJob.getCreatedBy() == null || !existingJob.getCreatedBy().equals(currentUser.getUserId())) {
            model.addAttribute("error", "You are not authorized to edit this job.");
            return "jobForRM"; // Trả về trang thông báo khi không có quyền sửa
        }

        if (result.hasErrors()) {
            List<String> availableSkills = Arrays.asList("Java", "Nodejs", ".Net", "C++", "Business analysis", "Communication");
            List<String> availableBenefits = Arrays.asList("Lunch", "25-day leave", "Healthcare insurance", "Hybrid working", "Travel");
            List<String> availableLevels = Arrays.asList("Fresher", "Junior", "Senior", "Leader", "Manager", "Vice Head");

            model.addAttribute("availableSkills", availableSkills);
            model.addAttribute("availableBenefits", availableBenefits);
            model.addAttribute("availableLevels", availableLevels);
            model.addAttribute("username", principal.getName());
            return "jobEdit"; // Trả về lại form sửa khi dữ liệu không hợp lệ
        }

        try {
            jobValidateProcessor.validate(result, dto); // Validate dữ liệu
            if (result.hasErrors()) {
                List<String> availableSkills = Arrays.asList("Java", "Nodejs", ".Net", "C++", "Business analysis", "Communication");
                List<String> availableBenefits = Arrays.asList("Lunch", "25-day leave", "Healthcare insurance", "Hybrid working", "Travel");
                List<String> availableLevels = Arrays.asList("Fresher", "Junior", "Senior", "Leader", "Manager", "Vice Head");

                model.addAttribute("job", dto);
                model.addAttribute("availableSkills", availableSkills);
                model.addAttribute("availableBenefits", availableBenefits);
                model.addAttribute("availableLevels", availableLevels);
                model.addAttribute("username", principal.getName());
                return "jobEdit"; // Trả về lại form sửa khi dữ liệu không hợp lệ
            }

            jobService.updateJob(jobId, dto, currentUser.getUserId()); // Cập nhật công việc
            return "redirect:/job/job-details/" + jobId; // Chuyển hướng đến trang chi tiết công việc sau khi cập nhật thành công
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update job."); // Xử lý lỗi và hiển thị thông báo
            List<String> availableSkills = Arrays.asList("Java", "Nodejs", ".Net", "C++", "Business analysis", "Communication");
            List<String> availableBenefits = Arrays.asList("Lunch", "25-day leave", "Healthcare insurance", "Hybrid working", "Travel");
            List<String> availableLevels = Arrays.asList("Fresher", "Junior", "Senior", "Leader", "Manager", "Vice Head");

            model.addAttribute("job", dto);
            model.addAttribute("availableSkills", availableSkills);
            model.addAttribute("availableBenefits", availableBenefits);
            model.addAttribute("availableLevels", availableLevels);
            model.addAttribute("username", principal.getName());
            return "jobEdit"; // Trả về lại form sửa khi xảy ra lỗi
        }
    }
    @GetMapping("/joblist")
    @PreAuthorize("hasAnyRole('RECRUITER','MANAGER','ADMIN')")
    public String getJobView(Model model, Principal principal,
                             @RequestParam(defaultValue = "0") Integer page,
                             @RequestParam(defaultValue = "5") Integer size,
                             @RequestParam(required = false) String keyword,
                             @RequestParam(required = false) String status) {
        if ("null".equalsIgnoreCase(keyword)) {
            keyword = null;
        }
        if ("null".equalsIgnoreCase(status)) {
            status = null;
        }

        JobViewPageForInterDto jobViewPageForInterDto;
        if ((keyword != null && !keyword.isEmpty()) || (status != null && !status.isEmpty())) {
            jobViewPageForInterDto = jobService.searchJobs(keyword, status, page, size);
        } else {
            jobViewPageForInterDto = jobService.pageJob(page, size);
        }

        model.addAttribute("jobs", jobViewPageForInterDto.getJobViewDtoList());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jobViewPageForInterDto.getTotalPage());
        model.addAttribute("totalElements", jobViewPageForInterDto.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("size", size);
        model.addAttribute("username", principal.getName());

        return "jobForRM";
    }

    @GetMapping("/job-details/{jobId}")
    @PreAuthorize("hasAnyRole('RECRUITER','MANAGER','ADMIN')")
    public String jobDetails(@PathVariable Integer jobId, Model model, Principal principal) {
        JobPostDto job = jobService.findById(jobId);
        model.addAttribute("job", job);
        model.addAttribute("username", principal.getName());
        return "jobDetail";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('RECRUITER','MANAGER','ADMIN')")
    public String deleteJob(@PathVariable("id") Integer id, Model model, Principal principal) {
        User currentUser = userRepository.findUserByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        try {
            JobPostDto job = jobService.findById(id);
            if (job == null || job.getCreatedBy() == null || !job.getCreatedBy().equals(currentUser.getUserId())) {
                model.addAttribute("error", "You are not authorized to delete this job.");
                return "jobForRM";
            } else {
                jobService.deleteJob(id, currentUser.getUserId());
                model.addAttribute("success", "Job deleted successfully");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Failed to delete job.");
        }
        return "redirect:/job/joblist";
    }

    @GetMapping("/interviewer")
    @PreAuthorize("hasRole('INTERVIEWER')")
    public String getJobViewInter(Model model,
                                  @RequestParam(defaultValue = "0") Integer page,
                                  @RequestParam(defaultValue = "5") Integer size,
                                  @RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) String status,
                                  Principal principal) {
        if ("null".equalsIgnoreCase(keyword)) {
            keyword = null;
        }
        if ("null".equalsIgnoreCase(status)) {
            status = null;
        }
        JobViewPageForInterDto jobViewPageForInterDto;
        if ((keyword != null && !keyword.isEmpty()) || (status != null && !status.isEmpty())) {
            jobViewPageForInterDto = jobService.searchJobs(keyword, status, page, size);
        } else {
            jobViewPageForInterDto = jobService.pageJob(page, size);
        }

        model.addAttribute("jobs", jobViewPageForInterDto.getJobViewDtoList());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jobViewPageForInterDto.getTotalPage());
        model.addAttribute("totalElements", jobViewPageForInterDto.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("size", size);
        model.addAttribute("username", principal.getName());

        return "jobForInter";
    }

    @GetMapping("/job-details-inter/{jobId}")
    @PreAuthorize("hasRole('INTERVIEWER')")
    public String jobDetailsInter(@PathVariable Integer jobId, Model model,Principal principal) {
        try {
            JobPostDto job = jobService.findById(jobId);
            model.addAttribute("job", job);
            model.addAttribute("username",principal.getName());;
            return "jobDetailInter";
        } catch (RuntimeException e) {
            return "jobNotFound";
        }
    }
}
