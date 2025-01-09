package com.example.demo.service;

import com.example.demo.database.entity.Candidate;
import com.example.demo.database.entity.Job;
import com.example.demo.database.entity.User;
import com.example.demo.model.candidate.CandidateDetailDto;
import com.example.demo.model.candidate.CandidatePostDto;
import com.example.demo.model.candidate.CandidateViewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CandidateService {
    // TODO
    void createCandidate(CandidatePostDto request);

    void updateCandidate(CandidatePostDto request);

    Page<CandidateViewDto> getAllCandidates(Pageable pageable);

    int getTotalPages(int pageSize);

    List<User> getAllRecruiters();

    CandidateDetailDto findById(Integer id);

    CandidatePostDto findCandidateById(Integer id);

    void deleteCandidate(String email);

    void banCandidate(Integer id);

    Page<CandidateViewDto> searchCandidates(String keyword, String status, Pageable pageable);

    Page<CandidateViewDto> searchCandidatesByRecruiter(String keyword, String status,
                                                       String recruiter, Pageable pageable);

    Page<CandidateViewDto> searchCandidatesByInterviewer(String keyword, String status,
                                                         String interviewer, Pageable pageable);

    Page<CandidateViewDto> getAllByInterviewer(Pageable pageable, String username);

    boolean isCandidateInterviewedBy(Integer candidateId, String username);

    boolean isCandidateRecruitedBy(Integer candidateId, String username);

    Page<CandidateViewDto> getAllByRecruiter(Pageable pageable, String username);
}
