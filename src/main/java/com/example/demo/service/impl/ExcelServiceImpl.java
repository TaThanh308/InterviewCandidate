package com.example.demo.service.impl;

import com.example.demo.database.entity.ExcelHelper;
import com.example.demo.database.entity.Job;
import com.example.demo.database.repository.JobRepository;
import com.example.demo.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    private JobRepository jobRepository;

    @Override
    public void save(MultipartFile file) {
        try {
            List<Job> jobs = ExcelHelper.excelToTutorials(file.getInputStream());
            jobRepository.saveAll(jobs);
        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        }
    }
}
