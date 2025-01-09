package com.example.demo.database.repository;

import com.example.demo.database.entity.Candidate;
import com.example.demo.database.entity.Interview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Integer> {
    Interview findByNotes(String note);

    @Query("SELECT i FROM Interview i " +
            "WHERE LOWER(CONCAT(i.result, i.candidate.name, i.job.title, i.scheduleTitle,i.scheduleFrom, i.scheduleTo, i.status, i.interviewers)) LIKE %:keyword% " +
            "AND LOWER(i.status) LIKE %:status%")
    Page<Interview> findByKeywordAndStatusContainingIgnoreCase(
            @Param("keyword") String keyword,
            @Param("status") String status,
            Pageable pageable);

    @Query("SELECT i FROM Interview i " +
            "WHERE LOWER(CONCAT(i.result, i.candidate.name, i.job.title, i.scheduleTitle,i.scheduleFrom, i.scheduleTo, i.status , i.interviewers)) LIKE %:keyword%")
    Page<Interview> findByKeywordInAnyFieldIgnoreCase(
            @Param("keyword") String keyword,
            Pageable pageable);


    Page<Interview> findByStatusContainingIgnoreCase(String status, Pageable pageable);

    @Query("SELECT i.scheduleTitle FROM Interview i ")
    List<String> findAllTitle();

    @Query("SELECT i.candidate.name FROM Interview i WHERE i.scheduleTitle = :interviewInfo")
    String findCanByInterviewInfo(@Param("interviewInfo") String interviewInfo);

    @Query("SELECT i.notes FROM Interview i WHERE i.scheduleTitle = :interviewInfo")
    String getInterviewNote(@Param("interviewInfo") String interviewInfo);

    @Query("SELECT i FROM Interview i WHERE i.scheduleTitle = :interviewInfo")
    Interview findByTitle(String interviewInfo);

    @Query("SELECT i FROM Interview i WHERE i.status = 'New'")
    List<Interview> findByScheduleTime(LocalDate scheduleTime);

//    Page<Interview> findByUsernameAndKeywordAndStatus(String username, String keyword, String status, Pageable pageable);
//    Page<Interview> findByUsername (String username, Pageable pageable);

    @Query("SELECT i FROM Interview i " +
            "JOIN i.interviewers ii JOIN ii.interviewer u " +
            "WHERE u.username = :username AND u.role = 'Interviewer'")
    Page<Interview> findInterviewsByInterviewerUsername(Pageable pageable, @Param("username") String username);

    Optional<Interview> findByScheduleTitle(String scheduleTitle);

    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END " +
            "FROM Interview i " +
            "JOIN InterviewerInterview ii ON i.interviewId = ii.interview.interviewId " +
            "JOIN User u ON ii.interviewer.userId = u.userId " +
            "WHERE i.interviewId = :interviewId AND u.username = :username")
    boolean existsByIdAndInterviewer(Integer interviewId, String username);


    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END " +
            "FROM Interview i " +
            "JOIN i.recruiter u " +
            "WHERE i.interviewId = :interviewId AND u.username = :username")
    boolean existsByIdAndRecruiter(@Param("interviewId") Integer interviewId, @Param("username") String username);






}
