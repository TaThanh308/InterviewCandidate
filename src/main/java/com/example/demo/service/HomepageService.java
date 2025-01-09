package com.example.demo.service;

import java.util.List;

public interface HomepageService {
    List<Object[]> countCandidatesByPosition();

    Integer getOpenCandidates();

    Integer getInterviewingCandidates();

    Integer getOfferingCandidates();

    Integer getBannedCandidates();

    Integer getAdminUsers();

    Integer getManagerUsers();

    Integer getRecruiterUsers();

    Integer getInterviewerUsers();

    List<Object[]> getInterviewersByInterviewCount(int limit);
}
