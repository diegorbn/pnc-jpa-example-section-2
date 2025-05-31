package com.ldar01.demoemployees.enums;

import lombok.Getter;

@Getter
public enum VacationStatusState {
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    private final String state;

    VacationStatusState(String state) {
        this.state = state;
    }
}
