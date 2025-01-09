package com.example.demo.database.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Data
public class Interview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interview_id")
    private Integer interviewId;
    private String scheduleTitle;
    private LocalDate scheduleTime;
    private LocalTime scheduleFrom;
    private LocalTime scheduleTo;
    private String location;
    private String meetingId;
    private String notes;
    private String result;
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    @ToString.Exclude
    private Candidate candidate;

    @OneToMany(mappedBy = "interview" , cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<InterviewerInterview> interviewers;

    @ManyToOne
    @JoinColumn(name = "recruiter_id", referencedColumnName = "user_id")
    @ToString.Exclude
    private User recruiter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    @ToString.Exclude
    private Job job;
}
