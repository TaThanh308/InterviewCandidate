package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;

public interface ExcelService {
    void save(MultipartFile file);
}
