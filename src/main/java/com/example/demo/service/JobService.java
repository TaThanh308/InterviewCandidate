package com.example.demo.service;

import com.example.demo.model.job.JobPostDto;
import com.example.demo.model.job.JobViewPageForInterDto;

public interface JobService {

    void createJob(JobPostDto request, Integer createdBy);

    JobViewPageForInterDto pageJob(int page, int size);

    JobViewPageForInterDto searchJobs(String keyword, String status, int page, int size);

    JobPostDto findById(Integer jobId);

    void deleteJob(Integer jobId, Integer userId);

    void updateJob(Integer jobId, JobPostDto jobPostDto, Integer userId);

}
