package com.example.demo.database.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    private String name;
    private String username;
    private LocalDate dob;
    private String gender;
    private String address;
    private String email;
    private String phone;
    private String role;
    private String department;
    private String status;
    private String notes;
    private String password;
    private String resetPasswordToken;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "interviewer")
    private List<InterviewerInterview> interviewsByInterviewer;

    @OneToMany(mappedBy = "recruiter", cascade = CascadeType.ALL)
    private List<Interview> interviewsByRecruiter;

    @OneToMany(mappedBy = "recruiter", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Candidate> candidates;

    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL)
    private List<Offer> offersByManager;

    @OneToMany(mappedBy = "recruiter", cascade = CascadeType.ALL)
    private List<Offer> offersByRecruiter;
}
