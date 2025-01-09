package com.example.demo.model.interview;

import com.example.demo.database.entity.Candidate;
import lombok.Data;

import java.util.List;

@Data
public class InterviewPageDto {
    private List<InterviewViewDto> interviewViewDtoList;
    private Integer totalPage;
    private Integer totalElements;
@Data
    public static class InterviewViewDto {
    public Integer id;
    private String title;
    private String candidateName;
    private String schedule;
    private String job;
    private String interviewer;
    private String result;
    private String status;
    private String notes;
    private String recruiterOwner;
    private String meetingID;
    private String location;
    }
}
