package com.example.demo.customError;

public class CustomExceptions {

    public static class NameRequiredException extends RuntimeException {
        public NameRequiredException(String message) {
            super(message);
        }
    }

    public static class EmailRequiredException extends RuntimeException {
        public EmailRequiredException(String message) {
            super(message);
        }
    }

    public static class GenderRequiredException extends RuntimeException {
        public GenderRequiredException(String message) {
            super(message);
        }
    }

    public static class PositionRequiredException extends RuntimeException {
        public PositionRequiredException(String message) {
            super(message);
        }
    }

    public static class SkillsRequiredException extends RuntimeException {
        public SkillsRequiredException(String message) {
            super(message);
        }
    }

    public static class RecruiterRequiredException extends RuntimeException {
        public RecruiterRequiredException(String message) {
            super(message);
        }
    }

    public static class StatusRequiredException extends RuntimeException {
        public StatusRequiredException(String message) {
            super(message);
        }
    }

    public static class HighestLevelRequiredException extends RuntimeException {
        public HighestLevelRequiredException(String message) {
            super(message);
        }
    }

    public static class DateOfBirthException extends RuntimeException {
        public DateOfBirthException(String message) {
            super(message);
        }
    }

    public static class PhoneRequiredException extends RuntimeException {
        public PhoneRequiredException(String message) {
            super(message);
        }
    }

    public static class EmailException extends RuntimeException {
        public EmailException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
