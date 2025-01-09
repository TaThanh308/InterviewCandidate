package com.example.demo.model.candidate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateViewDto {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private String position;
    private String recruiterUsername;
    private String status;
}
