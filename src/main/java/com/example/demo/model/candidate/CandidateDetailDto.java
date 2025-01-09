package com.example.demo.model.candidate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateDetailDto {
    private Integer id;
    private String name;
    private LocalDate dob;
    private String phone;
    private String email;
    private String address;
    private String gender;
    private String cv;
    private String position;
    private String skills;
    private String recruiter;
    private String status;
    private Integer yearOfExperience;
    private String highestLevel;
    private String notes;
}
