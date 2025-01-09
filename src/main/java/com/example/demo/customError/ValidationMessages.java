package com.example.demo.customError;

public final class ValidationMessages {
    private ValidationMessages() {}
    public static final String SCHEDULE_TITLE_NOT_BLANK = "Please enter Schedule Title";
    public static final String CANDIDATE_LIST_NOT_BLANK = "Please enter Candidate Name";
    public static final String SCHEDULE_TIME_NOT_NULL = "Please enter Schedule Time";
    public static final String SCHEDULE_FROM_NOT_NULL = "Please enter Schedule From";
    public static final String SCHEDULE_TO_NOT_NULL = "Please enter Schedule To";
    public static final String LOCATION_NOT_BLANK = "Please enter Location";
    public static final String JOB_NOT_BLANK = "Please enter Job";
    public static final String INTERVIEW_NOT_BLANK = "Please enter Interviewer";
    public static final String RECRUITER_OWNER_NOT_BLANK = "Please enter Recruiter Owner";
    public static final String MEETING_ID_NOT_BLANK = "Please enter Meeting ID";
    public static final String SCHEDULE_TIME_VALID = "Schedule time should not be yesterday or in the past";
    public static final String SCHEDULE_TIMES_VALID = "Start time must be before end time";
}
