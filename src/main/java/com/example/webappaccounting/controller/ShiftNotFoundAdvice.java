package com.example.webappaccounting.controller;

import com.example.webappaccounting.exceptions.ShiftNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ShiftNotFoundAdvice {
    @ResponseBody
    @ExceptionHandler(ShiftNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String shiftNotFoundHandler(ShiftNotFoundException exception) {
        return exception.getMessage();
    }
}

