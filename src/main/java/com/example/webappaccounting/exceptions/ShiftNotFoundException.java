package com.example.webappaccounting.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ShiftNotFoundException extends RuntimeException{
    public ShiftNotFoundException(Long id) {
        super("Не найдена смена с ID=" + id);
    }
}
