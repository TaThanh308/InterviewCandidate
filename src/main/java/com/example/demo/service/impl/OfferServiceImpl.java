package com.example.demo.service.impl;

import com.example.demo.database.entity.*;
import com.example.demo.database.repository.*;
import com.example.demo.model.offer.*;
import com.example.demo.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OfferServiceImpl implements OfferService {

    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private OfferRepository offerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InterviewRepository interviewRepository;
    @Autowired
    private InterviewerInterviewRepository interviewerInterviewRepository;



    @Override
    public String createOffer(OfferDto offerDto) {

       try {
           Offer offer = new Offer();

           Candidate candidate = candidateRepository.findByName(offerDto.getCandidate());
           if (candidate == null) {
               return "Candidate not found";
           }
           offer.setCandidate(candidate);

           User manager = userRepository.findByName(offerDto.getApprover());
           if (manager == null) {
               return "Manager not found";
           }
           offer.setManager(manager);

           Interview interview = interviewRepository.findByTitle(offerDto.getInterviewInfo());
           if (interview == null) {
               return "Interview not found";
           }
           offer.setInterview(interview);

           User recruiter = userRepository.findByName(offerDto.getRecruiter());
           if (recruiter == null) {
               return "Recruiter not found";
           }
           offer.setRecruiter(recruiter);

           Job job = interview.getJob();
           if (job == null) {
               return "Job not found";
           }
           offer.setJob(job);

           offer.setContractFrom(offerDto.getContractFrom());
           offer.setContractTo(offerDto.getContractTo());
           offer.setContractType(offerDto.getContractType());
           offer.setLevel(offerDto.getLevel());
           offer.setDepartment(offerDto.getDepartment());
           offer.setDueDate(offerDto.getDueDate());
           offer.setSalary(offerDto.getSalaryBasic());
           offer.setNotes(offerDto.getNotes());
           offer.setCreatedAt(LocalDateTime.now());
           offer.setUpdatedAt(LocalDateTime.now());
           offer.setStatus("Waiting For Approval");
           offer.getCandidate().setStatus("Waiting For Approval");

           offerRepository.save(offer);
       }catch (Exception ex){
           return "Offer already exists";
       }
        return "Done";
    }



    @Override
    public String updateOfferById(Integer id, OfferDto offerDto) {

        Optional<Offer> offerOptional = offerRepository.findById(id);
        Offer offer = offerOptional.get();
        if(!offer.getCandidate().getName().equals(offerDto.getCandidate()) || !offer.getManager().getName().equals(offerDto.getApprover()) || !offer.getInterview().getScheduleTitle().equals(offerDto.getInterviewInfo()) ){
            return " Candidate Name or Approver or Interview Info is not correct ";
        }

        offer.setContractFrom(offerDto.getContractFrom());
        offer.setContractTo(offerDto.getContractTo());
        offer.setContractType(offerDto.getContractType());
        offer.setLevel(offerDto.getLevel());
        offer.setDepartment(offerDto.getDepartment());
        offer.setDueDate(offerDto.getDueDate());
        offer.setSalary(offerDto.getSalaryBasic());
        offer.setNotes(offerDto.getNotes());
        offer.setUpdatedAt(LocalDateTime.now());
        offerRepository.save(offer);

        return "Done";
    }

    @Override
    public OfferGetViewDto getOfferView(Integer id) {
        OfferGetViewDto offerDto = new OfferGetViewDto();

        Optional<Offer> offer = offerRepository.findById(id);

        offerDto.setCandidate(offer.get().getCandidate().getName());
        offerDto.setApprover(offer.get().getManager().getName());
        offerDto.setPosition(offer.get().getJob().getTitle());
        offerDto.setInterviewInfo(offer.get().getInterview().getScheduleTitle());
        offerDto.setContractTo(offer.get().getContractTo());
        offerDto.setContractFrom(offer.get().getContractFrom());
        offerDto.setInterviewNotes(offer.get().getInterview().getNotes());
        offerDto.setNotes(offer.get().getInterview().getNotes());
        offerDto.setStatus(offer.get().getStatus());
        offerDto.setContractType(offer.get().getContractType());
        offerDto.setLevel(offer.get().getLevel());
        offerDto.setDepartment(offer.get().getDepartment());
        offerDto.setRecruiter(offer.get().getRecruiter().getName());
        offerDto.setDueDate(offer.get().getDueDate());
        offerDto.setSalaryBasic(offer.get().getSalary());
        offerDto.setNotes(offer.get().getNotes());
        offerDto.setUpdate(LocalDate.from(offer.get().getCreatedAt()));

        offerDto.setInterviewer(interviewerInterviewRepository.findInterviewerByInterviewId(offer.get().getInterview().getInterviewId()));

        return offerDto;
    }



    @Override
    public List<List<String>> getOfferListDefaultEntity() {
        List<List<String>> lists = new ArrayList<>();

        List<String> listCandidateName = candidateRepository.findAllName();

        List<String> listPosition = new ArrayList<>();
        listPosition.add("Backend Developer");
        listPosition.add("Business Analyst");
        listPosition.add("Tester");
        listPosition.add("HR");
        listPosition.add("Project manager");
        listPosition.add("Not available");

        List<String> listApprover = userRepository.findAlL();
        List<String> listInterviewInfo = interviewRepository.findAllTitle();

        List<String> listContractType = new ArrayList<>();
        listContractType.add("Trial 2 months");
        listContractType.add("Trainee 3 months");
        listContractType.add(" 1 year");
        listContractType.add("3 years and Unlimited");

        List<String> listLevel = new ArrayList<>();
        listLevel.add("Fresher");
        listLevel.add("Junior");
        listLevel.add("Senior");
        listLevel.add("Leader");
        listLevel.add("Manager");
        listLevel.add("Vice Head");
        List<String> listDepartment = new ArrayList<>();
        listDepartment.add("IT");
        listDepartment.add("HR");
        listDepartment.add("Finance");
        listDepartment.add("Communication");
        listDepartment.add("Marketing");
        listDepartment.add("Accounting");

        List<String> listRecruiter = userRepository.findAllRecruitersName();
        lists.add(listCandidateName);
        lists.add(listPosition);
        lists.add(listApprover);
        lists.add(listInterviewInfo);
        lists.add(listContractType);
        lists.add(listLevel);
        lists.add(listDepartment);
        lists.add(listRecruiter);
        return lists;
    }

    @Override
    public List<OfferGetEntity> getBySearch(String name, String department, String status ) {
        List<OfferGetEntity> offerGetEntities = new ArrayList<>();
        List<Integer> listId = candidateRepository.findByValueSearch(name);

        for (Integer id : listId) {
            Offer offer = new Offer();
            if (department == ""){
                offer = offerRepository.findAllBySearchOffersNotDepartment(id,status);
                if (status == ""){
                    offer = offerRepository.findAllBySearchOffersNotStatusAndNotDepartment(id);
                }
            }
            else if (status == ""){
                offer = offerRepository.findAllBySearchOffersNotStatus(id,department);
            }
            else {
                offer = offerRepository.findAllBySearchOffers(id,department,status);
            }

            if (offer != null) {
                OfferGetEntity offerGetEntity = new OfferGetEntity();

                if (offer != null && offer.getCandidate() != null) {
                    offerGetEntity.setCandidateName(offer.getCandidate().getName());
                    offerGetEntity.setEmail(offer.getCandidate().getEmail());
                } else {
                    offerGetEntity.setCandidateName("N/A");
                    offerGetEntity.setEmail("N/A");
                }

                offerGetEntity.setApprover(offer.getManager() != null ? offer.getManager().getName() : "N/A");
                offerGetEntity.setDepartment(offer.getDepartment());
                offerGetEntity.setNotes(offer.getNotes());
                offerGetEntity.setStatus(offer.getStatus());

                offerGetEntities.add(offerGetEntity);
            }
        }
        return offerGetEntities;
    }

    @Override
    public String getOfferToExcel(LocalDateTime from, LocalDateTime to) {
        Integer count = 0;

        List<OfferListToExcel> offerListToExcels = new ArrayList<>() ;
        List<Offer> offerList = offerRepository.findAllFromTo(from,to);
        for (Offer offer : offerList){
            OfferListToExcel offerListToExcel = new OfferListToExcel();

            offerListToExcel.setNo(++count);
            offerListToExcel.setCandidateId(offer.getCandidate().getCandidateId());
            offerListToExcel.setCandidateName(offer.getCandidate().getName());
            offerListToExcel.setApprovedBy(offer.getManager().getName());
            offerListToExcel.setContractType(offer.getContractType());
            offerListToExcel.setPosition(offer.getJob().getTitle());
            offerListToExcel.setLevel(offer.getLevel());
            offerListToExcel.setDepartment(offer.getDepartment());
            offerListToExcel.setRecruiterOwner(offer.getRecruiter().getName());
            offerListToExcel.setInterviewer(offer.getInterview().getScheduleTitle());
            offerListToExcel.setContractFrom(offer.getContractFrom());
            offerListToExcel.setContractTo(offer.getContractTo());
            offerListToExcel.setSalary(Integer.valueOf(offer.getSalary()));
            offerListToExcel.setInterviewNote(offer.getInterview().getNotes());
            offerListToExcel.setNote(offer.getNotes());
            offerListToExcels.add(offerListToExcel);
        }

        String filePath = "D:/final_Fsoft/interview_management_system_group_1/src/main/java/com/example/demo/service/impl/OfferList.xlsx";
        try {
            ExcelExporter.exportData(offerListToExcels, filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filePath;
    }

    @Override
    public String getInterviewNote(String candidate, String interviewName) {
        if(interviewRepository.findCanByInterviewInfo(interviewName).equals(candidate)){
            return interviewRepository.getInterviewNote(interviewName);
        }
        return "";
    }

    @Override
    public void editStatus(String status,Integer id) {

        Optional<Offer> offer = offerRepository.findById(id);
        Offer offer1 = offer.get();
        offer1.setStatus(status);
        offer1.getCandidate().setStatus(status);
        offerRepository.save(offer1);
    }

    @Override
    public UserLogin getUserLogin(Integer id) {
        UserLogin userLogin = new UserLogin();

        Optional<User> user = userRepository.findById(id);
        User getUser = user.get();

        userLogin.setUsername(getUser.getUsername());
        userLogin.setRole(getUser.getRole());

        return userLogin;
    }



    @Override
    public List<OfferGetEntity> findAllOffer() {
            List<Offer> offerList = offerRepository.findAll();
            List<OfferGetEntity> offerGetEntityList = new ArrayList<>();

            for (Offer offer : offerList) {
                OfferGetEntity offerGetEntity = new OfferGetEntity();
                offerGetEntity.setId(offer.getOfferId());
                if (offer.getCandidate() == null) {
                    continue;
                }
                offerGetEntity.setCandidateName(offer.getCandidate().getName());
                offerGetEntity.setEmail(offer.getCandidate().getEmail());
                if (offer.getManager() == null) {
                    continue;
                }
                offerGetEntity.setApprover(offer.getManager().getName());
                if (offer.getDepartment() == null) {
                    continue;
                }
                offerGetEntity.setDepartment(offer.getDepartment());
                if (offer.getNotes() == null) {
                    continue;
                }
                offerGetEntity.setNotes(offer.getNotes());
                if (offer.getStatus() == null) {
                    continue;
                }
                offerGetEntity.setStatus(offer.getStatus());
                offerGetEntityList.add(offerGetEntity);
            }

            return offerGetEntityList;
        }

}