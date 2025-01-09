package com.example.demo.model.offer;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class OfferGetViewDto {
    private String candidate;
    private String position;
    private String approver;
    private String interviewInfo;
    private List<String> interviewer;
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
    private LocalDate update;
}
