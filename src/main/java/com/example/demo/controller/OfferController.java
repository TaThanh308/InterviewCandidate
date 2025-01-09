package com.example.demo.controller;

import com.example.demo.model.offer.*;
import com.example.demo.service.OfferService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin

@RequestMapping("/offer")
//@PreAuthorize("hasRole('RECRUITER') or hasRole('MANAGER') or hasRole('ADMIN')")
public class OfferController {


    @Autowired
    private OfferService offerService;

    @GetMapping("/userLogin")
    public UserLogin getUserLogin(){
        return offerService.getUserLogin(1);
    }

    @PostMapping("/editStatus")
    public void editStatus(@RequestBody Map<String, String> status){
        offerService.editStatus(status.get("status"), Integer.valueOf(status.get("id")));
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadExcel() throws IOException {

        String filePath = "D:/final_Fsoft/interview_management_system_group_1/src/main/java/com/example/demo/service/impl/OfferList.xlsx";

        Resource resource = new FileSystemResource(filePath);

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Offer_list.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping("/getInterviewNotes")
    public String getInterviewNote(@RequestParam("candidateName") String candidateName,
                                   @RequestParam("interviewName") String interviewName) {
        return offerService.getInterviewNote(candidateName,interviewName);
    }


    @PostMapping("/postTest")
    public List<OfferGetEntity> searchPostRequest(@RequestBody String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonString);

        String name = "";
        String department = "";
        String status = "";
        JsonNode dataNode = rootNode.get("data");
        if (dataNode.isArray()) {
            name = dataNode.get(0).asText();
            department = dataNode.get(1).asText();
            status = dataNode.get(2).asText();
        }
        return  offerService.getBySearch(name,department,status);
    }


    @GetMapping("/getdefault")
    public List<List<String>> getDefault(){

        return offerService.getOfferListDefaultEntity();
    }



    @GetMapping("/view")
    public OfferGetViewDto getViewOffer(@RequestParam("id") String id){

        return offerService.getOfferView(Integer.valueOf(id));
    }

    @PostMapping("/exportExcel")
    public String exportExcel(@RequestBody @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime[] dates) {
        try {
            LocalDateTime from = dates[0];
            LocalDateTime to = dates[1];

            offerService.getOfferToExcel(from,to);
            return "Export success";
        } catch (DateTimeParseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format", ex);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred", ex);
        }
    }

    @PostMapping("/create")
    public String createOffer(@RequestBody List<String> create) {
        try {
            OfferDto offerDto = new OfferDto();

            setOfferCreateDto(offerDto, create);

            return offerService.createOffer(offerDto);
        } catch (Exception e) {
            return "Error creating offer";
        }

    }
    private void setOfferCreateDto(OfferDto offerDto, List<String> inputValueCreate) {
        offerDto.setCandidate(inputValueCreate.get(0));
        offerDto.setPosition(inputValueCreate.get(1));
        offerDto.setApprover(inputValueCreate.get(2));
        offerDto.setInterviewInfo(inputValueCreate.get(3));
        offerDto.setContractFrom(LocalDate.parse(inputValueCreate.get(4)));
        offerDto.setContractTo(LocalDate.parse(inputValueCreate.get(5)));
        offerDto.setInterviewNotes(inputValueCreate.get(6));
        offerDto.setContractType(inputValueCreate.get(7));
        offerDto.setLevel(inputValueCreate.get(8));
        offerDto.setDepartment(inputValueCreate.get(9));
        offerDto.setRecruiter(inputValueCreate.get(10));
        offerDto.setDueDate(LocalDate.parse(inputValueCreate.get(11)));
        offerDto.setSalaryBasic(inputValueCreate.get(12));
        offerDto.setNotes(inputValueCreate.get(13));
    }


    @PostMapping("/edit")
    public String updateOffer(@RequestBody OfferUpdateDto offerUpdateDto) {
        List<String> create = offerUpdateDto.getCreate();
        Integer id = offerUpdateDto.getId();

        OfferDto offerDto = new OfferDto();
        setOfferCreateDto(offerDto, create);

        return offerService.updateOfferById(id, offerDto);
    }

    @GetMapping("/getAllOffers")
    public List<OfferGetEntity> getAllOffers() {
        return offerService.findAllOffer();
    }

}