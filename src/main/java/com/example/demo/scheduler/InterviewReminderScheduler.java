package com.example.demo.scheduler;

import com.example.demo.database.entity.Interview;
import com.example.demo.database.repository.InterviewRepository;
import com.example.demo.service.InterviewService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class InterviewReminderScheduler {
    @Autowired
    private InterviewService interviewService;

    @Autowired
    private InterviewRepository interviewRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void sendInterviewReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Interview> interviews = interviewRepository.findByScheduleTime(tomorrow);
        for (Interview interview : interviews) {
            try {
                interviewService.sendInterviewDetailsToEmail(interview.getInterviewId());
            } catch (MessagingException e) {
                // Log error
                e.printStackTrace();
            }
        }
    }
}
