package com.example.demo.model.job;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class JobViewPageForInterDto {
    private List<JobViewDto> JobViewDtoList;
    private Integer totalPage;
    private Integer totalElements;

    @Data
    public static class JobViewDto {
        private Integer jobId;
        private String jobTitle;
        private String requiredSkills;
        private LocalDate startDate;
        private LocalDate endDate;
        private String level;
        private String status;
    }
}