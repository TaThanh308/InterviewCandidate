package com.example.demo.database.repository;

import com.example.demo.database.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Integer> {

    Page<Job> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Job> findByStatusContainingIgnoreCase(String status, Pageable pageable);

    Page<Job> findByTitleContainingIgnoreCaseAndStatusContainingIgnoreCase(String title, String status, Pageable pageable);

    @Query("SELECT j FROM Job j WHERE j.status = 'Open'")
    List<Job> findTitlesByOpen();

    Job findFirstByTitle(String title);

    @Query("SELECT j FROM Job j WHERE j.endDate = :date")
    List<Job> findByEndDate(@Param("date") LocalDate date);

    @Query("SELECT j FROM Job j WHERE j.startDate = :date")
    List<Job> findByStartDate(@Param("date") LocalDate date);
}
