package com.example.demo.database.repository;

import com.example.demo.database.entity.InterviewerInterview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InterviewerInterviewRepository extends JpaRepository<InterviewerInterview, Integer> {
    @Query("SELECT ii.interviewer.name FROM InterviewerInterview ii where ii.interview.interviewId = :idInterview ")
    List<String> findInterviewerByInterviewId(Integer idInterview);

    @Query("SELECT CONCAT(u.name, ' (', u.username, ')'), COUNT(ii) " +
            "FROM InterviewerInterview ii, User u " +
            "WHERE ii.interviewer.userId = u.userId " +
            "GROUP BY u.userId ORDER BY COUNT(ii) DESC")
    List<Object[]> findUsernameAndInterviewCount(Pageable pageable);

    @Query("SELECT CONCAT(u.name, ' (', u.username, ')'), COUNT(ii.id) " +
            "FROM InterviewerInterview ii " +
            "JOIN ii.interviewer u " +
            "GROUP BY u.name, u.username " +
            "ORDER BY COUNT(ii.id) DESC")
    List<Object[]> findInterviewerStatistics();
}
