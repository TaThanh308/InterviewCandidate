package com.example.demo.scheduler;

import com.example.demo.service.JobComparator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ScheduledJobService {

    @Autowired
    private JobComparator jobComparator;
    @Scheduled(cron = "0 */1 * * * *")
//    @Scheduled(cron = "0 0 0 * * *")
    public void setStartDate() {
        log.info("UpdateStatusJob start on: {}", LocalDateTime.now());
        jobComparator.changeStatusStartDate();
        log.info("UpdateStatusJob finished on: {}", LocalDateTime.now());
    }

    @Scheduled(cron = "0 0 0 * * *")
//    @Scheduled(cron = "0 */1 * * * *")
    public void setEndDate() {
        log.info("UpdateStatusJob start on: {}", LocalDateTime.now());
        jobComparator.changeStatusEndDate();
        log.info("UpdateStatusJob finished on: {}", LocalDateTime.now());
    }
}
