package com.example.demo.service;

import com.example.demo.database.entity.Candidate;
import com.example.demo.database.entity.Interview;
import com.example.demo.database.entity.Job;
import com.example.demo.database.entity.User;
import com.example.demo.model.interview.InterviewEditDto;
import com.example.demo.model.interview.InterviewPageDto;
import com.example.demo.model.interview.InterviewPostDto;
import com.example.demo.model.job.JobPostDto;
import com.example.demo.model.job.JobViewPageForInterDto;
import jakarta.mail.MessagingException;

import java.util.List;
import java.util.Optional;

public interface InterviewService {
    // TODO
    void createInterview(InterviewPostDto dto);
    InterviewPageDto pageInterview(int page, int size);

    InterviewPageDto searchInterviewr(String keyword, String status, int page, int size);

    List<Job> getAllJob();
    List<Candidate> getAllCandidate();

    List<User> getAllUser();

    List<User> getAllUserInterviewer();

    List<Candidate> getAllCandidateUser();
    InterviewPageDto.InterviewViewDto getInterviewById(Integer id);

    void EditInterview(InterviewEditDto request, Integer id) throws Exception;

    InterviewEditDto findById(Integer id);

    void cancelSchedule(Integer id);
    void Interviewed(InterviewPostDto request, Integer id) throws Exception;

    void sendInterviewDetailsToEmail(Integer interviewId) throws MessagingException;

    void updateInterviewStatus(Integer interviewId, String status);

    InterviewPageDto searchInterviewByUsername(String keyword, String status, int page, int size, String username);
    InterviewPageDto pageInterviewByUsername(int page, int size, String username);

    Optional<Interview> findByScheduleTitle(String scheduleTitle);

    boolean isInterviewInterviewedBy(Integer InterviewId, String username);
    boolean isInterviewRecruitedBy(Integer InterviewId, String username);

}
