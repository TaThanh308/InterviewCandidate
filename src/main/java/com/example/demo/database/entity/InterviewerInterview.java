package com.example.demo.database.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@Table(name = "interviewer_interview")
public class InterviewerInterview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "interview_id")
    @ToString.Exclude
    private Interview interview;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User interviewer;
}
