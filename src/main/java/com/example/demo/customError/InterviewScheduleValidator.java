package com.example.demo.customError;

import com.example.demo.database.entity.Interview;
import com.example.demo.model.interview.InterviewEditDto;
import com.example.demo.service.InterviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

@Component
public class InterviewScheduleValidator implements Validator {

    @Autowired
    private InterviewService interviewService;
    @Override
    public boolean supports(Class<?> clazz) {
        return InterviewEditDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        InterviewEditDto interviewDto = (InterviewEditDto) target;

        // Validate scheduleTitle
        if (interviewDto.getScheduleTitle() == null || interviewDto.getScheduleTitle().isEmpty()) {
            errors.rejectValue("scheduleTitle", "error.scheduleTitle.notBlank",
                    ValidationMessages.SCHEDULE_TITLE_NOT_BLANK);
        } else {
            Optional<Interview> existingInterview = interviewService.findByScheduleTitle(interviewDto.getScheduleTitle());
            if (existingInterview.isPresent() && !existingInterview.get().getInterviewId().equals(interviewDto.getId())) {
                errors.rejectValue("scheduleTitle", "error.scheduleTitle.duplicate",
                        "Schedule title already exists");
            }
        }

        // Validate scheduleTime

        // Validate scheduleTime
        if (interviewDto.getScheduleTime() == null) {
            errors.rejectValue("scheduleTime", "error.scheduleTime.notNull",
                    "Schedule time cannot be null");
        } else {
            if (interviewDto.getScheduleTime().isBefore(LocalDate.now())) {
                errors.rejectValue("scheduleTime", "error.scheduleTime.invalid",
                        "Schedule time cannot be in the past");
            }
            if (interviewDto.getScheduleTime().isEqual(LocalDate.now())) {
                errors.rejectValue("scheduleTime", "error.scheduleTime.today",
                        "Schedule time cannot be today");
            }
        }

        // Validate scheduleFrom
        if (interviewDto.getScheduleFrom() == null) {
            errors.rejectValue("scheduleFrom", "error.scheduleFrom.notNull",
                    ValidationMessages.SCHEDULE_FROM_NOT_NULL);
        }

// Validate scheduleTo
        if (interviewDto.getScheduleTo() == null) {
            errors.rejectValue("scheduleTo", "error.scheduleTo.notNull",
                    ValidationMessages.SCHEDULE_TO_NOT_NULL);
        } else if (interviewDto.getScheduleFrom() != null) {
            if (interviewDto.getScheduleTo().isBefore(interviewDto.getScheduleFrom())) {
                errors.rejectValue("scheduleTo", "error.scheduleTo.invalid",
                        ValidationMessages.SCHEDULE_TIMES_VALID);
            } else {
                Duration duration = Duration.between(interviewDto.getScheduleFrom(), interviewDto.getScheduleTo());
                if (duration.toMinutes() > 60) {
                    errors.rejectValue("scheduleTo", "error.scheduleTo.tooLong",
                            "The duration of the interview is too long (>60 minutes)");
                } else if (duration.toMinutes() < 30) {
                    errors.rejectValue("scheduleTo", "error.scheduleTo.tooShort",
                            "The duration of the interview is too short (<30 minutes)");
                }
            }
        }

        // Validate location
        if (interviewDto.getLocation() == null || interviewDto.getLocation().isEmpty()) {
            errors.rejectValue("location", "error.location.notBlank",
                    "Location cannot be blank.");
        }

        // Validate job
        if (interviewDto.getJob() == null || interviewDto.getJob().isEmpty()) {
            errors.rejectValue("job", "error.job.notBlank",
                    "Job cannot be blank.");
        }

        // Validate interview
        if (interviewDto.getInterview() == null || interviewDto.getInterview().isEmpty()) {
            errors.rejectValue("interview", "error.interview.notBlank",
                    "Interviewers cannot be blank.");
        }

        // Validate recruiterOwner
        if (interviewDto.getRecruiterOwner() == null || interviewDto.getRecruiterOwner().isEmpty()) {
            errors.rejectValue("recruiterOwner", "error.recruiterOwner.notBlank",
                    "Recruiter owner cannot be blank.");
        }

        // Validate meetingID
        if (interviewDto.getMeetingID() == null || interviewDto.getMeetingID().isEmpty()) {
            errors.rejectValue("meetingID", "error.meetingID.notBlank",
                    "Meeting ID cannot be blank.");
        }
    }
}
