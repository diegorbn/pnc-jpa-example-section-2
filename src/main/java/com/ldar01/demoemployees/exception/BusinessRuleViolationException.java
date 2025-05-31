package com.ldar01.demoemployees.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class BusinessRuleViolationException extends RuntimeException {
    private final List<String> errorMessages;

    public BusinessRuleViolationException(String message, List<String> errorMessages) {
        super(message);
        this.errorMessages = errorMessages;
    }
}
