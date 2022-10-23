package com.example.controller;

import com.example.models.BadInputParameters;
import com.example.models.ConflictDataException;
import com.example.models.ErrorResponse;
import com.example.models.ModelException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class BaseController {
    @ExceptionHandler(BadInputParameters.class)
    void handle(HttpServletResponse response, ModelException exception) throws IOException {
        sendResponse(response, HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }

    @ExceptionHandler(ConflictDataException.class)
    void handleConflictData(HttpServletResponse response, ModelException exception) throws IOException {
        sendResponse(response, HttpStatus.CONFLICT.value(), exception.getMessage());
    }

    void sendResponse(HttpServletResponse response, int status, String msg) throws IOException {
        response.setStatus(status);
        ObjectMapper mapper = new ObjectMapper();
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()))) {
            bw.write(mapper.writeValueAsString(new ErrorResponse(msg)));
        }
    }
}
