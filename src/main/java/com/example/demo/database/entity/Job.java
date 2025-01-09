package com.example.demo.database.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data

public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Integer jobId;

    private String title;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "salary_from")
    private Double salaryFrom;

    @Column(name = "salary_to")
    private Double salaryTo;

    @Column(name = "working_address")
    private String workingAddress;

    private String skills;
    private String benefits;
    private String level;
    private String description;
    private String status;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Interview> interviews;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<Candidate> candidates;

}
