package com.nour.SpringBootMovieReservationSystem.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final SecurityExceptionHandler handler;

    public CustomAccessDeniedHandler(SecurityExceptionHandler handler) {
        this.handler = handler;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {

        handler.writeErrorResponse(request, response, HttpStatus.FORBIDDEN,
                "You do not have permission to access this resource");
    }

}
