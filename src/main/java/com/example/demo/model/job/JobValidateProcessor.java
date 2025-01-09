package com.example.demo.model.job;

import com.example.demo.database.entity.Job;
import com.example.demo.database.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.regex.Pattern;

@Component
public class JobValidateProcessor {

    @Autowired
    JobRepository jobRepository;

    public BindingResult validate(BindingResult result, JobPostDto jobPostDto) {
        validateTitle(jobPostDto, result);
        validateDates(jobPostDto, result);
        validateSalaries(jobPostDto, result);
        validateSkills(jobPostDto, result);
        validateBenefits(jobPostDto, result);
        validateLevel(jobPostDto, result);
        validateStatus(jobPostDto, result);

        return result;
    }

    private void validateTitle(JobPostDto jobPostDto, BindingResult result) {
        String title = jobPostDto.getTitle();
        if (title == null || title.trim().isEmpty()) {
            result.rejectValue("title", "", "Title is mandatory");
        } else if (checkDuplicateTitle(title, jobPostDto.getJobId())) {
            result.rejectValue("title", "error.title", "Title already exists");
        }
    }

    private void validateDates(JobPostDto jobPostDto, BindingResult result) {
        LocalDate startDate = jobPostDto.getStartDate();
        LocalDate endDate = jobPostDto.getEndDate();

        if (startDate == null) {
            result.rejectValue("startDate", "", "Start date is mandatory");
        } else if (startDate.isBefore(LocalDate.now())) {
            result.rejectValue("startDate", "", "Start date cannot be in the past");
        } else if (endDate != null && startDate.isAfter(endDate)) {
            result.rejectValue("startDate", "", "Start date must be before end date");
        }

        if (endDate == null) {
            result.rejectValue("endDate", "", "End date is mandatory");
        } else if (startDate != null && endDate.isBefore(startDate)) {
            result.rejectValue("endDate", "", "End date must be after start date");
        }
    }

    private void validateSalaries(JobPostDto jobPostDto, BindingResult result) {
        String salaryFromStr = jobPostDto.getSalaryFrom();
        String salaryToStr = jobPostDto.getSalaryTo();

        if (salaryFromStr != null && salaryToStr != null) {
            if (!Pattern.matches("^(0|[1-9][0-9]*)$", salaryFromStr)) {
                result.rejectValue("salaryFrom", "", "Salary must be a valid number");
            }
            if (!Pattern.matches("^(0|[1-9][0-9]*)$", salaryToStr)) {
                result.rejectValue("salaryTo", "", "Salary must be a valid number");
            }

            try {
                int salaryFrom = Integer.parseInt(salaryFromStr);
                int salaryTo = Integer.parseInt(salaryToStr);
                if (salaryFrom > salaryTo) {
                    result.rejectValue("salaryFrom", "", "The 'from' value must be less than the 'to' value");
                }
            } catch (NumberFormatException e) {
                result.rejectValue("salaryFrom", "", "The 'from' and 'to' values must be valid numbers");
            }
        }
    }

    private void validateSkills(JobPostDto jobPostDto, BindingResult result) {
        if (jobPostDto.getSkills() == null || jobPostDto.getSkills().trim().isEmpty()) {
            result.rejectValue("skills", "", "Skills are mandatory");
        }
    }

    private void validateBenefits(JobPostDto jobPostDto, BindingResult result) {
        if (jobPostDto.getBenefits() == null || jobPostDto.getBenefits().trim().isEmpty()) {
            result.rejectValue("benefits", "", "Benefits are mandatory");
        }
    }

    private void validateLevel(JobPostDto jobPostDto, BindingResult result) {
        if (jobPostDto.getLevel() == null || jobPostDto.getLevel().trim().isEmpty()) {
            result.rejectValue("level", "", "Level is mandatory");
        }
    }

    private void validateStatus(JobPostDto jobPostDto, BindingResult result) {
        if (jobPostDto.getStatus() == null || jobPostDto.getStatus().trim().isEmpty()) {
            result.rejectValue("status", "", "Status is mandatory");
        }
    }

    public boolean checkDuplicateTitle(String title, Integer jobId) {
        Job job = jobRepository.findFirstByTitle(title);
        if (job != null && !job.getJobId().equals(jobId)) {
            return true;
        }
        return false;
    }
}
