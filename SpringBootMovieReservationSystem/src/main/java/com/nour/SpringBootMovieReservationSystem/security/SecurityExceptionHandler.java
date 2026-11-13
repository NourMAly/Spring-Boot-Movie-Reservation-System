package com.nour.SpringBootMovieReservationSystem.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.nour.SpringBootMovieReservationSystem.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Component
public class SecurityExceptionHandler {

    private final JsonMapper jsonMapper;

    public SecurityExceptionHandler(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    public void writeErrorResponse(HttpServletRequest request, HttpServletResponse response, HttpStatus status,
            String message) throws JacksonException, IOException {
        ErrorResponse error = new ErrorResponse(status.value(), status.getReasonPhrase(), message,
                request.getRequestURI(), LocalDateTime.now());

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        jsonMapper.writeValue(response.getOutputStream(), error);
    }
}
