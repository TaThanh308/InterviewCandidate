package com.example.demo.controller;

import ch.qos.logback.core.model.Model;
import com.example.demo.database.entity.ExcelHelper;
import com.example.demo.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FileController {

    @Autowired
    ExcelService excelService;

    @PostMapping("/files/upload")
    public String uploadFile(Model model, @RequestParam("file") MultipartFile file) {
        excelService.save(file);

        return "redirect:/job";
    }
}
