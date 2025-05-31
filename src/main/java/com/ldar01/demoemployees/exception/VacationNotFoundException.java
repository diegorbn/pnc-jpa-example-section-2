package com.ldar01.demoemployees.exception;

public class VacationNotFoundException extends RuntimeException {
    public VacationNotFoundException(String message) {
        super(message);
    }
}
