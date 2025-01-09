package com.example.demo.database.repository;

import com.example.demo.database.entity.User;
import com.example.demo.model.user.UserListDto;
import com.example.demo.model.user.UserViewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByUsername(String username);

    User findByEmail(String email);

    @Query("SELECT u FROM User u where u.username = :username ")
    User findByUsername(String username);

    @Query("SELECT u.username FROM User u where u.role ='Manager' AND u.status = 'Active'")
    List<String> findAlLApprover();

    @Query("SELECT u FROM User u where u.name = :name ")
    User findByName(String name);

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);

    @Query("SELECT u FROM User u")
    List<UserListDto> getAllContent();

    @Query("SELECT u FROM User u")
    List<UserViewDto> getAllUserDetails();

    @Query("SELECT u FROM User u WHERE u.role = 'Recruiter'")
    List<User> findAllRecruiters();

    @Query("SELECT u FROM User u WHERE u.role = 'Interviewer' AND u.status = 'Active'")
    List<User> findAllInterviewers();

    @Query("SELECT u.name FROM User u WHERE u.role = 'Recruiter'")
    List<String> findAllRecruitersName();

    @Query("SELECT u.name FROM User u")
    List<String> findAlL();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'Admin' AND u.status = 'Active'")
    Integer countAdminUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'Manager' AND u.status = 'Active'")
    Integer countManagerUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'Recruiter' AND u.status = 'Active'")
    Integer countRecruiterUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'Interviewer' AND u.status = 'Active'")
    Integer countInterviewerUsers();

    @Query("SELECT u FROM User u WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "u.username LIKE %:keyword% OR u.email LIKE %:keyword% OR u.phone LIKE %:keyword% OR " +
            "u.status LIKE %:keyword%) AND " +
            "(:role IS NULL OR :role = '' OR u.role = :role)")
    Page<User> searchByKeywordAndRole(String keyword, String role, Pageable pageable);

    Optional<User> findByResetPasswordToken(String token);

}

