package com.example.demo.model.offer;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OfferDto {
    private String candidate;
    private String position;
    private String approver;
    private String interviewInfo;
    private LocalDate contractFrom;
    private LocalDate contractTo;
    private String interviewNotes;
    private String contractType;
    private String level;
    private String department;
    private String recruiter;
    private LocalDate dueDate;
    private String salaryBasic;
    private String notes;
    private String status;
}
