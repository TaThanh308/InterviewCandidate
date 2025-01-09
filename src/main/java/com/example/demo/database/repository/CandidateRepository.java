package com.example.demo.database.repository;

import com.example.demo.database.entity.Candidate;
import com.example.demo.database.entity.Job;
import com.example.demo.database.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Integer>{
    Optional<Candidate> findByEmail(String email);

    @Query("SELECT c FROM Candidate c WHERE c.name = :name")
    Candidate findByName(@Param("name") String name);

    @Query("SELECT c.name FROM Candidate c")
    List<String> findAllName();

    @Query("SELECT c.candidateId FROM Candidate c WHERE c.name LIKE CONCAT('%', :data, '%')")
    List<Integer> findByValueSearch(@Param("data") String name);

    @Query("SELECT c FROM Candidate c WHERE c.status = 'Open'")
    List<Candidate> findAllOpen();

    @Query("SELECT c FROM Candidate c")
    List<Candidate> findByAll();
    Optional<Candidate> findByPhone(String phone);

    Candidate findFirstByName(String name);
    @Query("SELECT c FROM Candidate c WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "c.name LIKE %:keyword% OR c.email LIKE %:keyword% OR c.position LIKE %:keyword% OR " +
            "c.phone LIKE %:keyword% OR c.recruiter.username LIKE %:keyword%) AND " +
            "(:status IS NULL OR :status = '' OR c.status = :status)")
    Page<Candidate> searchByKeywordAndStatus(String keyword, String status, Pageable pageable);

    @Query("SELECT c FROM Candidate c WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "c.name LIKE %:keyword% OR c.email LIKE %:keyword% OR c.position LIKE %:keyword% OR " +
            "c.phone LIKE %:keyword% OR c.recruiter.username LIKE %:keyword%) AND " +
            "(:status IS NULL OR :status = '' OR c.status = :status) AND " +
            "c.recruiter.username = :recruiter")
    Page<Candidate> searchByKeywordAndStatusAndRecruiter(String keyword, String status,
                                                         String recruiter, Pageable pageable);

    @Query("SELECT c FROM Candidate c, User u, Interview i, InterviewerInterview ii WHERE " +
            "c.candidateId = i.candidate.candidateId AND " +
            "i.interviewId = ii.interview.interviewId AND " +
            "ii.interviewer.userId = u.userId AND " +
            "u.username = :interviewer AND " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "c.name LIKE %:keyword% OR c.email LIKE %:keyword% OR c.position LIKE %:keyword% OR " +
            "c.phone LIKE %:keyword% OR c.recruiter.username LIKE %:keyword%) AND " +
            "(:status IS NULL OR :status = '' OR c.status = :status)")
    Page<Candidate> searchByKeywordAndStatusAndInterviewer(String keyword, String status,
                                                           String interviewer, Pageable pageable);

    @Query("SELECT c.position, COUNT(c) FROM Candidate c GROUP BY c.position")
    List<Object[]> countCandidatesByPosition();

    @Query("SELECT c FROM Candidate c WHERE c.candidateId = :id")
    @NonNull
    Optional<Candidate> findById(@NonNull Integer id);

    @Query("SELECT c FROM Candidate c, User u, Interview i, InterviewerInterview ii " +
            "WHERE u.userId = ii.interviewer.userId AND ii.interview.interviewId = i.interviewId " +
            "AND i.candidate.candidateId = c.candidateId AND u.role = 'Interviewer' " +
            "AND u.username = :username")
    Page<Candidate> findAllByInterviewerUsername(Pageable pageable, @Param("username") String username);

    @Query("SELECT COUNT(c) FROM Candidate c WHERE c.status = 'Open'")
    Integer countOpenCandidates();

    @Query("SELECT COUNT(c) FROM Candidate c WHERE c.status LIKE '%interview%'")
    Integer countInterviewingCandidates();

    @Query("SELECT COUNT(c) FROM Candidate c WHERE c.status LIKE '%offer%' OR c.status LIKE '%approval%' " +
            "OR c.status LIKE '%response%'")
    Integer countOfferingCandidates();

    @Query("SELECT COUNT(c) FROM Candidate c WHERE c.status = 'Banned'")
    Integer countBannedCandidates();

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Candidate c JOIN Interview i ON c.candidateId = i.candidate.candidateId " +
            "JOIN InterviewerInterview ii ON i.interviewId = ii.interview.interviewId " +
            "JOIN User u ON ii.interviewer.userId = u.userId " +
            "WHERE c.candidateId = :candidateId AND u.username = :username")
    boolean existsByIdAndInterviewer(Integer candidateId, String username);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Candidate c " +
            "JOIN User u ON c.recruiter.userId = u.userId " +
            "WHERE c.candidateId = :candidateId AND u.username = :username")
    boolean existsByIdAndRecruiter(Integer candidateId, String username);

    @Query("SELECT c FROM Candidate c WHERE c.recruiter.username = :username")
    Page<Candidate> findAllByRecruiter(Pageable pageable, @Param("username") String username);


}
