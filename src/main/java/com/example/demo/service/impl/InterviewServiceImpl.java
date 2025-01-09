package com.example.demo.service.impl;

import com.example.demo.database.entity.*;
import com.example.demo.database.repository.*;
import com.example.demo.model.interview.InterviewCandidate;
import com.example.demo.model.interview.InterviewEditDto;
import com.example.demo.model.interview.InterviewPageDto;
import com.example.demo.model.interview.InterviewPostDto;
import com.example.demo.service.InterviewService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class InterviewServiceImpl implements InterviewService {
    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InterviewerInterviewRepository interviewerInterviewRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Override
    public void createInterview(InterviewPostDto dto) {
        Optional<Candidate> candidate = candidateRepository.findById(dto.getCandidates().get(0).getId());
        Job job = jobRepository.findFirstByTitle(dto.getJob());
        User recruiter = userRepository.findByUsername(dto.getRecruiterOwner());
        List<InterviewerInterview> interviewerInterviews = new ArrayList<>();

        if (dto.getInterview() != null) {
            List<String> interviewerUsernames = Arrays.asList(dto.getInterview().split(","));
            for (String username : interviewerUsernames) {
                User user = userRepository.findByUsername(username);
                if (user != null) {
                    InterviewerInterview interviewerInterview = new InterviewerInterview();
                    interviewerInterview.setInterviewer(user);
                    interviewerInterviews.add(interviewerInterview);
                }
            }
        }

        Interview interview = new Interview();
        interview.setScheduleTitle(dto.getScheduleTitle());
        interview.setScheduleTime(dto.getScheduleTime());
        interview.setCandidate(candidate.get());
        interview.setScheduleFrom(dto.getScheduleFrom());
        interview.setScheduleTo(dto.getScheduleTo());
        interview.setJob(job);
        interview.setLocation(dto.getLocation());
        interview.setRecruiter(recruiter);
        interview.setMeetingId(dto.getMeetingID());
        interview.setStatus("New");
        interview.setResult("N/A");
        interview.setCreatedAt(LocalDateTime.now());

        interview = interviewRepository.save(interview);

        for (InterviewerInterview interviewerInterview : interviewerInterviews) {
            interviewerInterview.setInterview(interview);
        }
        interviewerInterviewRepository.saveAll(interviewerInterviews);

        candidate.get().getInterviews().add(interview);
        candidate.get().setStatus("Waiting For Interview");
        candidateRepository.save(candidate.get());
    }



    @Override
    public InterviewPageDto pageInterview(int page, int size) {
        InterviewPageDto interviewPageDto = new InterviewPageDto();
        Page<Interview> interviewPage = interviewRepository.findAll(PageRequest.of(page, size));

        List<InterviewPageDto.InterviewViewDto> dtoResponseList = interviewPage.getContent().stream().map(entity -> {
            InterviewPageDto.InterviewViewDto dtoResponse = new InterviewPageDto.InterviewViewDto();
            dtoResponse.setId(entity.getInterviewId());

            String interviewers = entity.getInterviewers().stream()
                    .map(interviewerInterview -> interviewerInterview.getInterviewer().getUsername())
                    .collect(Collectors.joining(", "));
            dtoResponse.setInterviewer(interviewers);
            dtoResponse.setTitle(entity.getScheduleTitle());
            dtoResponse.setCandidateName(entity.getCandidate().getName());
            String schedule = entity.getScheduleTime().toString() + "  "
                    + entity.getScheduleFrom().toString() + " - "
                    + entity.getScheduleTo().toString();
            dtoResponse.setSchedule(schedule);
            dtoResponse.setJob(entity.getJob().getTitle());
            dtoResponse.setResult(entity.getResult());
            dtoResponse.setStatus(entity.getStatus());
            return dtoResponse;
        }).collect(Collectors.toList());

        interviewPageDto.setInterviewViewDtoList(dtoResponseList);
        interviewPageDto.setTotalPage(interviewPage.getTotalPages());
        interviewPageDto.setTotalElements((int) interviewPage.getTotalElements());
        return interviewPageDto;
    }

    @Override
    public InterviewPageDto searchInterviewr(String keyword, String status, int page, int size) {
        Page<Interview> interviewPage;
        Pageable pageable = PageRequest.of(page, size);

        if ((keyword != null && !keyword.isEmpty()) && (status != null && !status.isEmpty())) {
            interviewPage = interviewRepository.findByKeywordAndStatusContainingIgnoreCase(keyword, status, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            interviewPage = interviewRepository.findByKeywordInAnyFieldIgnoreCase(keyword, pageable);
        } else if (status != null && !status.isEmpty()) {
            interviewPage = interviewRepository.findByStatusContainingIgnoreCase(status, pageable);
        } else {
            interviewPage = interviewRepository.findAll(pageable);
        }

        List<InterviewPageDto.InterviewViewDto> dtoResponseList = interviewPage.getContent().stream().map(entity -> {
            InterviewPageDto.InterviewViewDto dtoResponse = new InterviewPageDto.InterviewViewDto();
            dtoResponse.setId(entity.getInterviewId());

            String interviewers = entity.getInterviewers().stream()
                    .map(interviewerInterview -> interviewerInterview.getInterviewer().getUsername())
                    .collect(Collectors.joining(", "));
            dtoResponse.setInterviewer(interviewers);
            dtoResponse.setTitle(entity.getScheduleTitle());
            dtoResponse.setCandidateName(entity.getCandidate().getName());
            String schedule = entity.getScheduleTime().toString() + "  "
                    + entity.getScheduleFrom().toString() + " - "
                    + entity.getScheduleTo().toString();

            dtoResponse.setSchedule(schedule);
            dtoResponse.setJob(entity.getJob().getTitle());
            dtoResponse.setResult(entity.getResult());
            dtoResponse.setStatus(entity.getStatus());
            return dtoResponse;

        }).collect(Collectors.toList());
        InterviewPageDto interviewPageDto = new InterviewPageDto();
        interviewPageDto.setInterviewViewDtoList(dtoResponseList);
        interviewPageDto.setTotalPage(interviewPage.getTotalPages());
        interviewPageDto.setTotalElements((int) interviewPage.getTotalElements());
        return interviewPageDto;
    }

    @Override
    public List<Job> getAllJob() {
        return jobRepository.findTitlesByOpen();
    }

    @Override
    public List<Candidate> getAllCandidate() {
        return candidateRepository.findAllOpen();
    }

    @Override
    public List<Candidate> getAllCandidateUser() {
        return candidateRepository.findByAll();
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAllRecruiters();
    }

    @Override
    public List<User> getAllUserInterviewer() {
        return userRepository.findAllInterviewers();
    }


    @Override
    public InterviewPageDto.InterviewViewDto getInterviewById(Integer id) {
        Interview interview = interviewRepository.findById(id).orElse(null);
        if (interview == null) {
            return null;
        }

        InterviewPageDto.InterviewViewDto dto = new InterviewPageDto.InterviewViewDto();
        dto.setId(interview.getInterviewId());
        dto.setTitle(interview.getScheduleTitle());
        dto.setCandidateName(interview.getCandidate().getName());
        String schedule = interview.getScheduleTime().toString() + " Form "
                + interview.getScheduleFrom().toString() + " To "
                + interview.getScheduleTo().toString();

        dto.setSchedule(schedule);
        dto.setJob(interview.getJob().getTitle());
        String interviewers = interview.getInterviewers().stream()
                .map(interviewerInterview -> interviewerInterview.getInterviewer().getUsername())
                .collect(Collectors.joining(","));
        dto.setInterviewer(interviewers);
        dto.setRecruiterOwner(interview.getRecruiter().getUsername());
        dto.setMeetingID(interview.getMeetingId());
        dto.setNotes(interview.getNotes());
        dto.setStatus(interview.getStatus());
        dto.setResult(interview.getResult());
        dto.setLocation(interview.getLocation());
        return dto;
    }


    @Override
    public void EditInterview(InterviewEditDto request, Integer id) throws Exception {
        try {
            Optional<Interview> interviewOptional = interviewRepository.findById(id);
            if (interviewOptional.isEmpty()) {
                throw new Exception("Interview not found with id: " + id);
            }
            Interview interview = interviewOptional.get();
            Job job = jobRepository.findFirstByTitle(request.getJob());
            User recruiter = userRepository.findByUsername(request.getRecruiterOwner());

            interview.getInterviewers().clear();

            // Add new interviewers
            if (request.getInterview() != null) {
                List<String> interviewerUsernames = Arrays.asList(request.getInterview().split(","));
                System.out.println("Interviewer usernames: " + interviewerUsernames);
                for (String username : interviewerUsernames) {
                    User user = userRepository.findByUsername(username);
                    if (user != null) {
                        InterviewerInterview interviewerInterview = new InterviewerInterview();
                        interviewerInterview.setInterviewer(user);
                        interviewerInterview.setInterview(interview); // Ensure the relationship is set
                        interview.getInterviewers().add(interviewerInterview);
                    }
                }
            }

            interview.setScheduleTitle(request.getScheduleTitle());
            interview.setScheduleTime(request.getScheduleTime());
            interview.setScheduleFrom(request.getScheduleFrom());
            interview.setScheduleTo(request.getScheduleTo());
            interview.setJob(job);
            interview.setLocation(request.getLocation());
            interview.setRecruiter(recruiter);
            interview.setMeetingId(request.getMeetingID());
            interview.setNotes(request.getNotes());
            interview.setUpdatedAt(LocalDateTime.now());
            interviewRepository.save(interview);

        } catch (DataIntegrityViolationException e) {
            throw new Exception("Data integrity violation while updating interview with id: " + id, e);
        } catch (Exception e) {
            throw new Exception("Error editing interview with id: " + id, e);
        }
    }



    @Override
    public InterviewEditDto findById(Integer id) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Interview not found"));

        InterviewEditDto interviewPostDto = new InterviewEditDto();
        interviewPostDto.setId(interview.getInterviewId());
        interviewPostDto.setJob(interview.getJob().getTitle());


        List<String> interviewerNames = interview.getInterviewers()
                .stream()
                .map(interviewerInterview -> interviewerInterview.getInterviewer().getUsername())
                .collect(Collectors.toList());
        interviewPostDto.setInterview(String.join(",", interviewerNames));

        interviewPostDto.setRecruiterOwner(interview.getRecruiter().getUsername());
        interviewPostDto.setCandidateName(interview.getCandidate().getName());
        interviewPostDto.setScheduleTitle(interview.getScheduleTitle());
        interviewPostDto.setScheduleTime(interview.getScheduleTime());
        interviewPostDto.setScheduleFrom(interview.getScheduleFrom());
        interviewPostDto.setScheduleTo(interview.getScheduleTo());
        interviewPostDto.setLocation(interview.getLocation());
        interviewPostDto.setMeetingID(interview.getMeetingId());
        interviewPostDto.setResult(interview.getResult());
        interviewPostDto.setStatus(interview.getStatus());
        interviewPostDto.setNotes(interview.getNotes());

        return interviewPostDto;
    }


    @Transactional
    public void cancelSchedule(Integer id) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Interview not found: " + id));
        Candidate candidate = interview.getCandidate();
        candidate.setStatus("Cancelled Interview");
        candidateRepository.save(candidate);

        interview.setStatus("Cancelled");
        interviewRepository.save(interview);
    }

    @Transactional
    public void Interviewed(InterviewPostDto request, Integer id) throws Exception {
        try {
            Optional<Interview> interviewOptional = interviewRepository.findById(id);
            if (interviewOptional.isEmpty()) {
                throw new Exception("Interview not found with id: " + id);
            }

            Interview interview = interviewOptional.get();
            interview.setResult(request.getResult());
            interview.setNotes(request.getNotes());
            interview.setUpdatedAt(LocalDateTime.now());
            interview.setStatus("Interviewed");
            interviewRepository.save(interview);

            if ("Failed".equalsIgnoreCase(request.getResult())) {
                Candidate candidate = interview.getCandidate();
                candidate.setStatus("Failed Interview");
                candidateRepository.save(candidate);
            }
            else if ("Passed".equalsIgnoreCase(request.getResult())) {
                Candidate candidate = interview.getCandidate();
                candidate.setStatus("Passed Interview");
                candidateRepository.save(candidate);
            }

            interviewRepository.save(interview);

        } catch (DataIntegrityViolationException e) {
            throw new Exception("Data integrity violation while updating interview with id: " + id, e);
        } catch (Exception e) {
            throw new Exception("Error editing interview with id: " + id, e);
        }
    }

    @Override
    public void sendInterviewDetailsToEmail(Integer interviewId) throws MessagingException {
        Interview interview = interviewRepository.findById(interviewId).orElseThrow(() -> new IllegalArgumentException("Invalid interview ID"));
        String recipientEmail = interview.getCandidate().getEmail();

        Context context = new Context();
        context.setVariable("title", interview.getScheduleTitle());
        context.setVariable("candidateName", interview.getCandidate().getName());
        context.setVariable("scheduleTime", interview.getScheduleTime());
        context.setVariable("scheduleFrom", interview.getScheduleFrom());
        context.setVariable("scheduleTo", interview.getScheduleTo());
        context.setVariable("job", interview.getJob().getTitle());
        context.setVariable("interviewer", interview.getInterviewers().stream().map(interviewerInterview -> interviewerInterview.getInterviewer().getUsername()).collect(Collectors.joining(",")));
        context.setVariable("location", interview.getLocation());
        context.setVariable("recruiterOwner", interview.getRecruiter().getUsername());
        context.setVariable("meetingID", interview.getMeetingId());
        context.setVariable("status", interview.getStatus());
        context.setVariable("notes", interview.getNotes());
        String body = templateEngine.process("interviewEmail", context);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(recipientEmail);
            helper.setSubject("Interview Schedule Details");
            helper.setText(body, true);
            mailSender.send(message);
            updateInterviewStatus(interviewId, "Invited");
        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send email", e);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void updateInterviewStatus(Integer interviewId, String status) {
        Optional<Interview> interviewOpt = interviewRepository.findById(interviewId);
        if (interviewOpt.isPresent()) {
            Interview interview = interviewOpt.get();
            interview.setStatus(status);
            interviewRepository.save(interview);
        }
    }

    @Override
    public InterviewPageDto searchInterviewByUsername(String keyword, String status, int page, int size, String username) {
        Page<Interview> interviewPage;
        Pageable pageable = PageRequest.of(page, size);

        if ((keyword != null && !keyword.isEmpty()) && (status != null && !status.isEmpty())) {
            interviewPage = interviewRepository.findByKeywordAndStatusContainingIgnoreCase(keyword, status, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            interviewPage = interviewRepository.findByKeywordInAnyFieldIgnoreCase(keyword, pageable);
        } else if (status != null && !status.isEmpty()) {
            interviewPage = interviewRepository.findByStatusContainingIgnoreCase(status, pageable);
        } else {
            interviewPage = interviewRepository.findAll(pageable);
        }

        List<InterviewPageDto.InterviewViewDto> dtoResponseList = interviewPage.getContent().stream().map(entity -> {
            InterviewPageDto.InterviewViewDto dtoResponse = new InterviewPageDto.InterviewViewDto();
            dtoResponse.setId(entity.getInterviewId());

            String interviewers = entity.getInterviewers().stream()
                    .map(interviewerInterview -> interviewerInterview.getInterviewer().getUsername())
                    .collect(Collectors.joining(", "));
            dtoResponse.setInterviewer(interviewers);
            dtoResponse.setTitle(entity.getScheduleTitle());
            dtoResponse.setCandidateName(entity.getCandidate().getName());
            String schedule = entity.getScheduleTime().toString() + "  "
                    + entity.getScheduleFrom().toString() + " - "
                    + entity.getScheduleTo().toString();

            dtoResponse.setSchedule(schedule);
            dtoResponse.setJob(entity.getJob().getTitle());
            dtoResponse.setResult(entity.getResult());
            dtoResponse.setStatus(entity.getStatus());
            return dtoResponse;

        }).collect(Collectors.toList());
        InterviewPageDto interviewPageDto = new InterviewPageDto();
        interviewPageDto.setInterviewViewDtoList(dtoResponseList);
        interviewPageDto.setTotalPage(interviewPage.getTotalPages());
        interviewPageDto.setTotalElements((int) interviewPage.getTotalElements());
        return interviewPageDto;
    }

    @Override
    public InterviewPageDto pageInterviewByUsername(int page, int size, String username) {
        InterviewPageDto interviewPageDto = new InterviewPageDto();
        Pageable pageable = PageRequest.of(page, size);
        Page<Interview> interviewPage = interviewRepository.findInterviewsByInterviewerUsername( pageable, username);

        List<InterviewPageDto.InterviewViewDto> dtoResponseList = interviewPage.getContent().stream().map(entity -> {
            InterviewPageDto.InterviewViewDto dtoResponse = new InterviewPageDto.InterviewViewDto();
            dtoResponse.setId(entity.getInterviewId());
            String interviewers = entity.getInterviewers().stream()
                    .map(interviewerInterview -> interviewerInterview.getInterviewer().getUsername())
                    .collect(Collectors.joining(", "));
            dtoResponse.setInterviewer(interviewers);
            dtoResponse.setTitle(entity.getScheduleTitle());
            dtoResponse.setCandidateName(entity.getCandidate().getName());  // Đây là phần có thể gây lỗi
            String schedule = entity.getScheduleTime().toString() + "  "
                    + entity.getScheduleFrom().toString() + " - "
                    + entity.getScheduleTo().toString();
            dtoResponse.setSchedule(schedule);
            dtoResponse.setJob(entity.getJob().getTitle());
            dtoResponse.setResult(entity.getResult());
            dtoResponse.setStatus(entity.getStatus());
            return dtoResponse;
        }).collect(Collectors.toList());


        interviewPageDto.setInterviewViewDtoList(dtoResponseList);
        interviewPageDto.setTotalPage(interviewPage.getTotalPages());
        interviewPageDto.setTotalElements((int) interviewPage.getTotalElements());
        return interviewPageDto;
    }

    @Override
    public Optional<Interview> findByScheduleTitle(String scheduleTitle) {
        return interviewRepository.findByScheduleTitle(scheduleTitle);
    }

    @Override
    public boolean isInterviewInterviewedBy(Integer InterviewId, String username) {
        return interviewRepository.existsByIdAndInterviewer(InterviewId, username);
    }

    @Override
    public boolean isInterviewRecruitedBy(Integer InterviewId, String username) {
        if (InterviewId == null || username == null) {
            return false;
        }
        return interviewRepository.existsByIdAndRecruiter(InterviewId, username);
    }
}

