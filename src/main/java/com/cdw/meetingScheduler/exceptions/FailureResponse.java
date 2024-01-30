package com.cdw.meetingScheduler.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FailureResponse {
    private int httpStatusCode;
    private String message;
}
