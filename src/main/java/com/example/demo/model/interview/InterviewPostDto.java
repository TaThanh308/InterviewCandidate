package com.example.demo.model.interview;

import com.example.demo.database.entity.Candidate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewPostDto {
    private Integer id;
    private String scheduleTitle;
    private LocalDate scheduleTime;
    private LocalTime scheduleFrom;
    private LocalTime scheduleTo;
    private String location;
    private String job;
    private String interview;
    private String recruiterOwner;
    private String meetingID;
    private String notes;
    private String status;
    private String result;
    private List<InterviewCandidate> candidates;

}
