package com.example.demo.service.impl;

import com.example.demo.database.repository.CandidateRepository;
import com.example.demo.database.repository.InterviewerInterviewRepository;
import com.example.demo.database.repository.UserRepository;
import com.example.demo.service.HomepageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomepageServiceImpl implements HomepageService {
    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InterviewerInterviewRepository interviewerInterviewRepository;

    @Override
    public List<Object[]> countCandidatesByPosition() {
        return candidateRepository.countCandidatesByPosition();
    }

    @Override
    public Integer getOpenCandidates() {
        return candidateRepository.countOpenCandidates();
    }

    @Override
    public Integer getInterviewingCandidates() {
        return candidateRepository.countInterviewingCandidates();
    }

    @Override
    public Integer getOfferingCandidates() {
        return candidateRepository.countOfferingCandidates();
    }

    @Override
    public Integer getBannedCandidates() {
        return candidateRepository.countBannedCandidates();
    }

    @Override
    public Integer getAdminUsers() {
        return userRepository.countAdminUsers();
    }

    @Override
    public Integer getManagerUsers() {
        return userRepository.countManagerUsers();
    }

    @Override
    public Integer getRecruiterUsers() {
        return userRepository.countRecruiterUsers();
    }

    @Override
    public Integer getInterviewerUsers() {
        return userRepository.countInterviewerUsers();
    }

    @Override
    public List<Object[]> getInterviewersByInterviewCount(int limit) {
        return interviewerInterviewRepository.findUsernameAndInterviewCount(PageRequest.of(0, limit));
    }
}
