package com.example.demo.model.interview;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InterviewCandidate {
    private Integer id;
    private String candidateName;
    private String email;
}
