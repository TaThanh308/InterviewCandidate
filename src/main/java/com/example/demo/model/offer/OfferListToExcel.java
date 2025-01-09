package com.example.demo.model.offer;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OfferListToExcel {
    private Integer no;
    private Integer candidateId;
    private String candidateName;
    private String ApprovedBy;
    private String contractType;
    private String position;
    private String level;
    private String department;
    private String recruiterOwner;
    private String interviewer;
    private LocalDate contractFrom;
    private LocalDate contractTo;
    private Integer salary;
    private String interviewNote;
    private String note;
}
