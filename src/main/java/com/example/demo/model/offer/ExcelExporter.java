package com.example.demo.model.offer;
import com.example.demo.database.entity.Candidate;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExcelExporter  {

    public static void exportData(List<OfferListToExcel> toExcels, String filePath) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            String[] headers = {"No.", "Candidate ID", "Candidate Name", "Approved By", "Contract Type",
                    "Position", "Level", "Department", "Recruiter Owner", "Interviewer",
                    "Contract Start From", "Contract To", "Basic Salary", "Interview Notes", "Notes"};
            writer.writeNext(headers);

            for (OfferListToExcel toExcel : toExcels) {
                String[] row = {
                        String.valueOf(toExcel.getNo()),
                        String.valueOf(toExcel.getCandidateId()),
                        toExcel.getCandidateName(),
                        toExcel.getApprovedBy(),
                        toExcel.getContractType(),
                        toExcel.getPosition(),
                        toExcel.getLevel(),
                        toExcel.getDepartment(),
                        toExcel.getRecruiterOwner(),
                        toExcel.getInterviewer(),
                        String.valueOf(toExcel.getContractFrom()),
                        String.valueOf(toExcel.getContractTo()),
                        String.valueOf(toExcel.getSalary()),
                        toExcel.getInterviewNote(),
                        toExcel.getNote()
                };
                writer.writeNext(row);
            }
        }
    }
}
