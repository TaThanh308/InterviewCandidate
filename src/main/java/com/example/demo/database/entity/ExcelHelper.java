package com.example.demo.database.entity;

import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
@Log4j2
public class ExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = {"Id", "Title", "Description", "Published"};
    static String SHEET = "Sheet1";

    public static boolean hasExcelFormat(MultipartFile file) {

        if (!TYPE.equals(file.getContentType())) {
            return false;
        }

        return true;
    }


    public static List<Job> excelToTutorials(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);

            Sheet sheet = workbook.getSheet(SHEET);
            Iterator<Row> rows = sheet.iterator();

            List<Job> jobList = new ArrayList<>();
            int totalRow = 0;
            int countFail = 0;
            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }
                totalRow++;
                Iterator<Cell> cellsInRow = currentRow.iterator();

                Job job = new Job();
                job.setStatus("Draft");

                int cellIdx = 0;

                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();

                    try {
                        switch (cellIdx) {
                            case 1:

                                job.setTitle(currentCell.getStringCellValue());
                                break;

                            case 2:

                                job.setSkills(currentCell.getStringCellValue());
                                break;

                            case 3:
                                job.setStartDate(LocalDate.parse(String.valueOf(currentCell.getLocalDateTimeCellValue().toLocalDate())));
                                break;

                            case 4:
                                job.setEndDate(LocalDate.parse(String.valueOf(currentCell.getLocalDateTimeCellValue().toLocalDate())));
                                break;

                            case 5:
                                job.setSalaryFrom(Double.valueOf(String.valueOf(currentCell.getNumericCellValue())));
                                break;

                            case 6:
                                job.setSalaryTo(Double.valueOf(String.valueOf(currentCell.getNumericCellValue())));
                                break;

                            case 7:
                                job.setBenefits(currentCell.getStringCellValue());
                                break;

                            case 8:
                                job.setWorkingAddress(currentCell.getStringCellValue());
                                break;

                            case 9:
                                job.setLevel(currentCell.getStringCellValue());
                                break;

                            case 10:
                                job.setDescription(currentCell.getStringCellValue());
                                break;


                            default:
                                break;
                        }
                    } catch (Exception e) {
                        countFail++;
                    }


                    cellIdx++;
                }

                jobList.add(job);
            }

            workbook.close();
            Integer countSuccess = calculation(totalRow, countFail);
            log.info("Total row: " + totalRow);
            log.info("Count fail: " + countFail);
            log.info("Count success: " + countSuccess);


            return jobList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }

    }

    private static Integer calculation(Integer totalRow, Integer countFail) {
        return totalRow - countFail;
    }
}