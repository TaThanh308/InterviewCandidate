package com.example.demo.model.offer;

import lombok.Data;

@Data
public class OfferGetEntity {
    private Integer id;
    private String candidateName;
    private String email;
    private String approver;
    private String department;
    private String notes;
    private String status;
}
