package com.example.demo.database.repository;

import com.example.demo.database.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Integer> {


    @Query("SELECT o FROM Offer o")
    List<Offer> findAllOffers();

    @Query("SELECT o FROM Offer o WHERE o.candidate.candidateId = :id and o.department = :department and o.status = :status")
    Offer findAllBySearchOffers(@Param("id") Integer candidateId,@Param("department") String department,@Param("status") String status);

    @Query("SELECT o FROM Offer o WHERE o.candidate.candidateId = :id and o.status = :status")
    Offer findAllBySearchOffersNotDepartment(@Param("id") Integer candidateId,@Param("status") String status);

    @Query("SELECT o FROM Offer o WHERE o.candidate.candidateId = :id and o.department = :department")
    Offer findAllBySearchOffersNotStatus(@Param("id") Integer candidateId,@Param("department") String department);

    @Query("SELECT o FROM Offer o WHERE o.candidate.candidateId = :id")
    Offer findAllBySearchOffersNotStatusAndNotDepartment(@Param("id") Integer candidateId);


    @Query("SELECT o.offerId FROM Offer o where o.candidate.name = :candidate AND o.job.title = :position AND o.manager.name = :approver AND o.recruiter.name = :recruiter AND o.interview.scheduleTitle = :interviewInfo")
    Offer findToCreate(@Param("candidate") String candidate,
                       @Param("position") String position,
                       @Param("approver") String approver,
                       @Param("recruiter") String recruiter,
                       @Param("interviewInfo") String interviewInfo
    );

    @Query("SELECT o FROM Offer o WHERE o.createdAt BETWEEN :from AND :to")
    List<Offer> findAllFromTo(@Param("from")LocalDateTime from,@Param("to") LocalDateTime to);
}
