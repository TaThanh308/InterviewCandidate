package com.example.demo.service.impl;

import com.example.demo.database.entity.Job;
import com.example.demo.database.repository.JobRepository;
import com.example.demo.model.job.JobPostDto;
import com.example.demo.model.job.JobViewPageForInterDto;
import com.example.demo.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;

    @Override
    public void createJob(JobPostDto request, Integer createdBy) {
        Job job = mapToEntity(request);
        job.setCreatedBy(createdBy); // Set the creator's ID
        jobRepository.save(job);
    }

    @Override
    public JobViewPageForInterDto pageJob(int page, int size) {
        Page<Job> jobPage = jobRepository.findAll(PageRequest.of(page, size));
        return mapToJobViewPageForInterDto(jobPage);
    }

    @Override
    public JobViewPageForInterDto searchJobs(String keyword, String status, int page, int size) {
        Page<Job> jobPage;
        Pageable pageable = PageRequest.of(page, size);

        if (keyword != null && !keyword.isEmpty() && status != null && !status.isEmpty()) {
            jobPage = jobRepository.findByTitleContainingIgnoreCaseAndStatusContainingIgnoreCase(keyword, status, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            jobPage = jobRepository.findByTitleContainingIgnoreCase(keyword, pageable);
        } else if (status != null && !status.isEmpty()) {
            jobPage = jobRepository.findByStatusContainingIgnoreCase(status, pageable);
        } else {
            jobPage = jobRepository.findAll(pageable);
        }

        return mapToJobViewPageForInterDto(jobPage);
    }

    @Override
    public JobPostDto findById(Integer jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        return mapToDto(job);
    }

    @Override
    public void deleteJob(Integer jobId, Integer userId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getCreatedBy().equals(userId)) {
            throw new RuntimeException("User does not have permission to delete this job");
        }

        jobRepository.deleteById(jobId);
    }

    @Override
    public void updateJob(Integer jobId, JobPostDto jobPostDto, Integer userId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getCreatedBy().equals(userId)) {
            throw new RuntimeException("User does not have permission to update this job");
        }

        updateEntity(job, jobPostDto);
        jobRepository.save(job);
    }

    private Job mapToEntity(JobPostDto request) {
        Job job = new Job();
        job.setTitle(request.getTitle());
        job.setStartDate(request.getStartDate());
        job.setEndDate(request.getEndDate());
        job.setSalaryFrom(Double.valueOf(request.getSalaryFrom()));
        job.setSalaryTo(Double.valueOf(request.getSalaryTo()));
        job.setWorkingAddress(request.getWorkingAddress());
        job.setSkills(request.getSkills());
        job.setBenefits(request.getBenefits());
        job.setLevel(request.getLevel());
        job.setDescription(request.getDescription());
        job.setStatus(request.getStatus());
        return job;
    }

    private JobPostDto mapToDto(Job job) {
        JobPostDto jobPostDto = new JobPostDto();
        jobPostDto.setJobId(job.getJobId());
        jobPostDto.setTitle(job.getTitle());
        jobPostDto.setStartDate(job.getStartDate());
        jobPostDto.setEndDate(job.getEndDate());
        jobPostDto.setSalaryFrom(String.valueOf(job.getSalaryFrom()));
        jobPostDto.setSalaryTo(String.valueOf(job.getSalaryTo()));
        jobPostDto.setWorkingAddress(job.getWorkingAddress());
        jobPostDto.setSkills(job.getSkills());
        jobPostDto.setBenefits(job.getBenefits());
        jobPostDto.setLevel(job.getLevel());
        jobPostDto.setDescription(job.getDescription());
        jobPostDto.setStatus(job.getStatus());
        jobPostDto.setCreatedBy(job.getCreatedBy());
        return jobPostDto;
    }

    private void updateEntity(Job job, JobPostDto jobPostDto) {
        job.setTitle(jobPostDto.getTitle());
        job.setStartDate(jobPostDto.getStartDate());
        job.setEndDate(jobPostDto.getEndDate());
        job.setSalaryFrom(Double.valueOf(jobPostDto.getSalaryFrom()));
        job.setSalaryTo(Double.valueOf(jobPostDto.getSalaryTo()));
        job.setWorkingAddress(jobPostDto.getWorkingAddress());
        job.setSkills(jobPostDto.getSkills());
        job.setBenefits(jobPostDto.getBenefits());
        job.setLevel(jobPostDto.getLevel());
        job.setDescription(jobPostDto.getDescription());
        job.setStatus(jobPostDto.getStatus());
    }

    private JobViewPageForInterDto mapToJobViewPageForInterDto(Page<Job> jobPage) {
        List<JobViewPageForInterDto.JobViewDto> dtoResponseList = jobPage.getContent().stream().map(job -> {
            JobViewPageForInterDto.JobViewDto dto = new JobViewPageForInterDto.JobViewDto();
            dto.setJobId(job.getJobId());
            dto.setJobTitle(job.getTitle());
            dto.setRequiredSkills(job.getSkills());
            dto.setStartDate(job.getStartDate());
            dto.setEndDate(job.getEndDate());
            dto.setLevel(job.getLevel());
            dto.setStatus(job.getStatus() != null ? job.getStatus() : "Draft");
            return dto;
        }).collect(Collectors.toList());

        JobViewPageForInterDto jobViewPageForInterDto = new JobViewPageForInterDto();
        jobViewPageForInterDto.setJobViewDtoList(dtoResponseList);
        jobViewPageForInterDto.setTotalPage(jobPage.getTotalPages());
        jobViewPageForInterDto.setTotalElements((int) jobPage.getTotalElements());
        return jobViewPageForInterDto;
    }
}
