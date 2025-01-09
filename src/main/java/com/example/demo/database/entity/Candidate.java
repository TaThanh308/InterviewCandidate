package com.example.demo.database.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "candidate_id")
    private Integer candidateId;
    private String name;
    private String gender;
    private String address;
    private String email;
    private String phone;
    private String status;
    private LocalDate dob;
    private String cv;
    private String notes;
    private String position;
    private String skills;

    @Column(name = "year_of_experience")
    private Integer yearOfExperience;

    @Column(name = "highest_level")
    private String highestLevel;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id", referencedColumnName = "user_id")
    @ToString.Exclude
    private User recruiter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Job job;

    @OneToMany(mappedBy = "candidate")
    @ToString.Exclude
    private List<Interview> interviews;
}
