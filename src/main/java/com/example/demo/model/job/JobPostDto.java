package com.example.demo.model.job;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobPostDto {
    private Integer jobId;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String workingAddress;
    private String salaryFrom;
    private String salaryTo;
    private String skills;
    private String benefits;
    private String level;
    private String description;
    private String status;
    private Integer createdBy;
}
