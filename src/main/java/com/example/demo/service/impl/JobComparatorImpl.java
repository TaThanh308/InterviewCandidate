package com.example.demo.service.impl;

import com.example.demo.database.entity.Job;
import com.example.demo.database.repository.JobRepository;
import com.example.demo.service.JobComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class JobComparatorImpl implements JobComparator {
    @Autowired
    private JobRepository jobRepository;

    @Override
    public void changeStatusEndDate() {
        List<Job> jobList = jobRepository.findByEndDate(LocalDate.now());
        for (Job job : jobList) {
            if ("OPEN".equals(job.getStatus())) {
                job.setStatus("CLOSED");
                jobRepository.save(job);
            }
        }
    }

    @Override
    public void changeStatusStartDate() {
        List<Job> jobList = jobRepository.findByStartDate(LocalDate.now());
        for (Job job : jobList) {
            if ("DRAFT".equals(job.getStatus())) {
                job.setStatus("OPEN");
                jobRepository.save(job);
            }
        }
    }
}
