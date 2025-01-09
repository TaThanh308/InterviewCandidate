package com.example.demo.service.impl;

import com.example.demo.customError.CustomExceptions.*;
import com.example.demo.database.entity.Candidate;
import com.example.demo.database.entity.Job;
import com.example.demo.database.entity.User;
import com.example.demo.database.repository.CandidateRepository;
import com.example.demo.database.repository.JobRepository;
import com.example.demo.database.repository.UserRepository;
import com.example.demo.model.candidate.CandidateDetailDto;
import com.example.demo.model.candidate.CandidatePostDto;
import com.example.demo.model.candidate.CandidateViewDto;
import com.example.demo.service.CandidateService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CandidateServiceImpl implements CandidateService {
    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${spring.file.upload-dir}")
    private String uploadDir;

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern emailPattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
        return emailPattern.matcher(email).find();
    }

    public boolean isValidField(CandidatePostDto request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new NameRequiredException("Full name is required");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new EmailRequiredException("Email is required");
        }
        if (request.getGender() == null || request.getGender().trim().isEmpty()) {
            throw new GenderRequiredException("Gender is required");
        }
        if (request.getPosition() == null || request.getPosition().trim().isEmpty()) {
            throw new PositionRequiredException("Position is required");
        }
        if (request.getSkills() == null || request.getSkills().isEmpty()) {
            throw new SkillsRequiredException("Skills are required");
        }
        if (request.getRecruiter() == null || request.getRecruiter().trim().isEmpty()) {
            throw new RecruiterRequiredException("Recruiter is required");
        }
        if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            throw new StatusRequiredException("Status is required");
        }
        if (request.getHighestLevel() == null || request.getHighestLevel().trim().isEmpty()) {
            throw new HighestLevelRequiredException("Highest level is required");
        }
        if (request.getDob() != null && !request.getDob().isBefore(LocalDate.now())) {
            throw new DateOfBirthException("Date of Birth must be in the past");
        }
        if (!isValidEmail(request.getEmail())) {
            throw new EmailRequiredException("Email is not valid format");
        }
        return true;
    }


    @Override
    public void createCandidate(CandidatePostDto request) {
        if (isValidField(request)) {
            Optional<Candidate> candidateOptional = candidateRepository.findByEmail(request.getEmail());
            if (candidateOptional.isPresent()) {
                throw new EmailRequiredException("Email already exists");
            }

            if (request.getPhone() != null && !request.getPhone().isEmpty()) {
                Optional<Candidate> candidateOptionalPhone = candidateRepository.findByPhone(request.getPhone());
                if (candidateOptionalPhone.isPresent()) {
                    throw new PhoneRequiredException("Phone already exists");
                }
            }

            User recruiter = userRepository.findByUsername(request.getRecruiter());

            Candidate createCandidate = new Candidate();
            createCandidate.setName(request.getName());
            createCandidate.setDob(request.getDob());
            createCandidate.setEmail(request.getEmail());
            createCandidate.setPhone(request.getPhone());
            createCandidate.setGender(request.getGender());
            createCandidate.setAddress(request.getAddress());

            MultipartFile cvFile = request.getCv();
            String fileName = cvFile.getOriginalFilename();

            try {
                if (cvFile != null && !fileName.isEmpty()) {
                    File uploadDirFile = new File(uploadDir);
                    if (!uploadDirFile.exists()) {
                        uploadDirFile.mkdirs();
                    }
                    File uploadFile = new File(uploadDir, fileName);
                    cvFile.transferTo(uploadFile);
                }
            } catch (Exception e) {
                throw new RuntimeException("Could not save file");
            }
            createCandidate.setCv(fileName);

            createCandidate.setPosition(request.getPosition());
            createCandidate.setSkills(request.getSkills());
            createCandidate.setStatus(request.getStatus());
            createCandidate.setYearOfExperience(request.getYearOfExperience());
            createCandidate.setHighestLevel(request.getHighestLevel());
            createCandidate.setRecruiter(recruiter);
            createCandidate.setNotes(request.getNotes());
            createCandidate.setCreatedAt(LocalDateTime.now());
            candidateRepository.save(createCandidate);
        }
    }

    @Override
    public CandidateDetailDto findById(Integer id) {
        Candidate candidate = candidateRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Candidate not found"));
        CandidateDetailDto dto = new CandidateDetailDto();
        dto.setId(candidate.getCandidateId());
        dto.setName(candidate.getName());
        dto.setDob(candidate.getDob());
        dto.setPhone(candidate.getPhone());
        dto.setEmail(candidate.getEmail());
        dto.setAddress(candidate.getAddress());
        dto.setGender(candidate.getGender());
        dto.setCv(candidate.getCv());
        dto.setPosition(candidate.getPosition());
        dto.setSkills(candidate.getSkills());
        dto.setRecruiter(candidate.getRecruiter().getUsername());
        dto.setStatus(candidate.getStatus());
        dto.setYearOfExperience(candidate.getYearOfExperience());
        dto.setHighestLevel(candidate.getHighestLevel());
        dto.setNotes(candidate.getNotes());
        return dto;
    }

    @Override
    public CandidatePostDto findCandidateById(Integer id) {
        Optional<Candidate> candidateOptional = candidateRepository.findById(id);

        if (candidateOptional.isEmpty()) {
            throw new RuntimeException("Candidate not found");
        }

        Candidate candidate = candidateOptional.get();
        CandidatePostDto candidatePostDto = new CandidatePostDto();
        candidatePostDto.setId(candidate.getCandidateId());
        candidatePostDto.setName(candidate.getName());
        candidatePostDto.setDob(candidate.getDob());
        candidatePostDto.setPhone(candidate.getPhone());
        candidatePostDto.setEmail(candidate.getEmail());
        candidatePostDto.setAddress(candidate.getAddress());
        candidatePostDto.setGender(candidate.getGender());
        candidatePostDto.setPosition(candidate.getPosition());
        candidatePostDto.setSkills(candidate.getSkills());
        candidatePostDto.setRecruiter(candidate.getRecruiter().getUsername());
        candidatePostDto.setStatus(candidate.getStatus());
        candidatePostDto.setYearOfExperience(candidate.getYearOfExperience());
        candidatePostDto.setHighestLevel(candidate.getHighestLevel());
        candidatePostDto.setNotes(candidate.getNotes());

        String cvFileName = candidate.getCv();
        if (cvFileName != null && !cvFileName.isEmpty()) {
            String filePath = uploadDir + File.separator + cvFileName;

            try {
                File file = new File(filePath);
                FileInputStream input = new FileInputStream(file);
                MultipartFile cvFile = new MockMultipartFile("cv", file.getName(), "application/octet-stream", IOUtils.toByteArray(input));
                candidatePostDto.setCv(cvFile);
            } catch (Exception e) {
                throw new RuntimeException("Could not create MultipartFile for CV");
            }
        }

        return candidatePostDto;
    }

    public void updateCandidate(CandidatePostDto request) {
        Optional<Candidate> candidateOptional = candidateRepository.findById(request.getId());

        if (candidateOptional.isPresent()) {
            Candidate existingCandidate = candidateOptional.get();

            if (request.getName() == null || request.getName().trim().isEmpty()) {
                throw new NameRequiredException("Full name is required");
            }
            existingCandidate.setName(request.getName());

            if (request.getDob() != null) {
                if (request.getDob().isAfter(LocalDate.now())) {
                    throw new DateOfBirthException("Date of Birth must be in the past");
                }
                existingCandidate.setDob(request.getDob());
            }

            if (request.getPhone() != null && !request.getPhone().isEmpty() && !request.getPhone().equals(existingCandidate.getPhone())) {
                if (!request.getPhone().matches("^[0-9]*$")) {
                    throw new PhoneRequiredException("Phone must be number");
                }
                Optional<Candidate> candidateOptionalPhone = candidateRepository.findByPhone(request.getPhone());
                if (candidateOptionalPhone.isPresent() && !candidateOptionalPhone.get().getCandidateId().equals(existingCandidate.getCandidateId())) {
                    throw new PhoneRequiredException("Phone already exists");
                }
                existingCandidate.setPhone(request.getPhone());
            }

            existingCandidate.setGender(request.getGender());
            existingCandidate.setAddress(request.getAddress());

            if (!isValidEmail(request.getEmail())) {
                throw new EmailRequiredException("Email is not valid format");
            } else if (!request.getEmail().equals(existingCandidate.getEmail())) {
                Optional<Candidate> candidateOptionalEmail = candidateRepository.findByEmail(request.getEmail());
                if (candidateOptionalEmail.isPresent() && !candidateOptionalEmail.get().getCandidateId().equals(existingCandidate.getCandidateId())) {
                    throw new EmailRequiredException("Email already exists");
                }
            }
            existingCandidate.setEmail(request.getEmail());

            MultipartFile cvFile = request.getCv();
            if (cvFile != null && !cvFile.isEmpty()) {
                try {
                    String uploadDir = "C:\\Users\\MyPC\\Desktop\\SAVE_FILE";
                    File uploadDirFile = new File(uploadDir);
                    if (!uploadDirFile.exists()) {
                        uploadDirFile.mkdirs();
                    }

                    String fileName = cvFile.getOriginalFilename();
                    String newFileName = fileName;
                    File uploadFile = new File(uploadDir, newFileName);
                    int counter = 1;
                    while (uploadFile.exists()) {
                        String name = fileName.substring(0, fileName.lastIndexOf('.'));
                        String extension = fileName.substring(fileName.lastIndexOf('.'));
                        newFileName = name + "_" + counter + extension;
                        uploadFile = new File(uploadDir, newFileName);
                        counter++;
                    }

                    cvFile.transferTo(uploadFile);
                    existingCandidate.setCv(newFileName);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to save file", e);
                }
            }

            existingCandidate.setPosition(request.getPosition());
            existingCandidate.setSkills(request.getSkills());

            User recruiter = userRepository.findByUsername(request.getRecruiter());
            existingCandidate.setRecruiter(recruiter);

            existingCandidate.setYearOfExperience(request.getYearOfExperience());
            existingCandidate.setHighestLevel(request.getHighestLevel());
            existingCandidate.setNotes(request.getNotes());

            existingCandidate.setUpdatedAt(LocalDateTime.now());
            candidateRepository.save(existingCandidate);
        } else {
            throw new RuntimeException("Candidate not found");
        }
    }

    @Override
    public Page<CandidateViewDto> getAllCandidates(Pageable pageable) {
        Page<Candidate> candidatePage = candidateRepository.findAll(pageable);
        List<CandidateViewDto> candidateViewList = candidatePage.stream().map(candidate -> {
            CandidateViewDto candidateViewDto = new CandidateViewDto();
            candidateViewDto.setId(candidate.getCandidateId());
            candidateViewDto.setName(candidate.getName());
            candidateViewDto.setEmail(candidate.getEmail());
            candidateViewDto.setPhone(candidate.getPhone());
            candidateViewDto.setPosition(candidate.getPosition());
            candidateViewDto.setRecruiterUsername(candidate.getRecruiter().getUsername());
            candidateViewDto.setStatus(candidate.getStatus());
            return candidateViewDto;
        }).collect(Collectors.toList());

        return new PageImpl<>(candidateViewList, pageable, candidatePage.getTotalElements());
    }

    @Override
    public Page<CandidateViewDto> getAllByInterviewer(Pageable pageable, String username) {
        Page<Candidate> candidatePage = candidateRepository.findAllByInterviewerUsername(pageable, username);
        List<CandidateViewDto> candidateViewList = candidatePage.stream().map(candidate -> {
            CandidateViewDto candidateViewDto = new CandidateViewDto();
            candidateViewDto.setId(candidate.getCandidateId());
            candidateViewDto.setName(candidate.getName());
            candidateViewDto.setEmail(candidate.getEmail());
            candidateViewDto.setPhone(candidate.getPhone());
            candidateViewDto.setPosition(candidate.getPosition());
            candidateViewDto.setRecruiterUsername(candidate.getRecruiter().getUsername());
            candidateViewDto.setStatus(candidate.getStatus());
            return candidateViewDto;
        }).collect(Collectors.toList());

        return new PageImpl<>(candidateViewList, pageable, candidatePage.getTotalElements());
    }

    @Override
    public Page<CandidateViewDto> getAllByRecruiter(Pageable pageable, String username) {
        Page<Candidate> candidatePage = candidateRepository.findAllByRecruiter(pageable, username);
        List<CandidateViewDto> candidateViewList = candidatePage.stream().map(candidate -> {
            CandidateViewDto candidateViewDto = new CandidateViewDto();
            candidateViewDto.setId(candidate.getCandidateId());
            candidateViewDto.setName(candidate.getName());
            candidateViewDto.setEmail(candidate.getEmail());
            candidateViewDto.setPhone(candidate.getPhone());
            candidateViewDto.setPosition(candidate.getPosition());
            candidateViewDto.setRecruiterUsername(candidate.getRecruiter().getUsername());
            candidateViewDto.setStatus(candidate.getStatus());
            return candidateViewDto;
        }).collect(Collectors.toList());

        return new PageImpl<>(candidateViewList, pageable, candidatePage.getTotalElements());
    }

    @Override
    public Page<CandidateViewDto> searchCandidates(String keyword, String status, Pageable pageable) {
        Page<Candidate> candidatePage = candidateRepository.searchByKeywordAndStatus(keyword, status, pageable);
        List<CandidateViewDto> candidateViewList = candidatePage.stream().map(this::convertToDto).collect(Collectors.toList());

        return new PageImpl<>(candidateViewList, pageable, candidatePage.getTotalElements());
    }

    @Override
    public Page<CandidateViewDto> searchCandidatesByRecruiter(String keyword, String status,
                                                              String recruiter, Pageable pageable) {
        Page<Candidate> candidatePage = candidateRepository.searchByKeywordAndStatusAndRecruiter(keyword, status, recruiter, pageable);
        List<CandidateViewDto> candidateViewList = candidatePage.stream().map(this::convertToDto).collect(Collectors.toList());

        return new PageImpl<>(candidateViewList, pageable, candidatePage.getTotalElements());
    }

    @Override
    public Page<CandidateViewDto> searchCandidatesByInterviewer(String keyword, String status,
                                                                String interviewer, Pageable pageable) {
        Page<Candidate> candidatePage = candidateRepository.searchByKeywordAndStatusAndInterviewer(keyword, status, interviewer, pageable);
        List<CandidateViewDto> candidateViewList = candidatePage.stream().map(this::convertToDto).collect(Collectors.toList());

        return new PageImpl<>(candidateViewList, pageable, candidatePage.getTotalElements());
    }

    private CandidateViewDto convertToDto(Candidate candidate) {
        CandidateViewDto candidateViewDto = new CandidateViewDto();
        candidateViewDto.setId(candidate.getCandidateId());
        candidateViewDto.setName(candidate.getName());
        candidateViewDto.setEmail(candidate.getEmail());
        candidateViewDto.setPhone(candidate.getPhone());
        candidateViewDto.setPosition(candidate.getPosition());
        candidateViewDto.setRecruiterUsername(candidate.getRecruiter().getUsername());
        candidateViewDto.setStatus(candidate.getStatus());
        return candidateViewDto;
    }

    @Override
    public void deleteCandidate(String email) {
        Optional<Candidate> candidateOptional = candidateRepository.findByEmail(email);
        if (candidateOptional.isPresent()) {
            Candidate candidate = candidateOptional.get();
            if (candidate.getStatus().equals("Open")) {
                candidateRepository.delete(candidateOptional.get());
            } else {
                throw new RuntimeException("Candidate cannot delete because status is not Open");
            }
        } else {
            throw new RuntimeException("Candidate not found");
        }
    }

    @Override
    public void banCandidate(Integer id) {
        Optional<Candidate> candidateOptional = candidateRepository.findById(id);
        if (candidateOptional.isPresent()) {
            Candidate candidate = candidateOptional.get();
            if (!candidate.getStatus().equals("Banned")) {
                candidate.setStatus("Banned");
                candidateRepository.save(candidate);
            } else {
                throw new RuntimeException("Candidate already banned");
            }
        } else {
            throw new RuntimeException("Candidate not found");
        }
    }

    @Override
    public boolean isCandidateInterviewedBy(Integer candidateId, String username) {
        return candidateRepository.existsByIdAndInterviewer(candidateId, username);
    }

    @Override
    public boolean isCandidateRecruitedBy(Integer candidateId, String username) {
        return candidateRepository.existsByIdAndRecruiter(candidateId, username);
    }

    @Override
    public List<User> getAllRecruiters() {
        return userRepository.findAllRecruiters();
    }

    @Override
    public int getTotalPages(int pageSize) {
        return (int) Math.ceil((double) candidateRepository.count() / pageSize);
    }
}
