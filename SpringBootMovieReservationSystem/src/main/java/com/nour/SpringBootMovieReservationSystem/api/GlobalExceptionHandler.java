package com.nour.SpringBootMovieReservationSystem.api;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.nour.SpringBootMovieReservationSystem.CustomException.UserNotFoundException;
import com.nour.SpringBootMovieReservationSystem.apiservice.RefreshCookies;
import com.nour.SpringBootMovieReservationSystem.CustomException.HallAlreadyOccupiedException;
import com.nour.SpringBootMovieReservationSystem.CustomException.HallNotFoundException;
import com.nour.SpringBootMovieReservationSystem.CustomException.InvalidRefreshTokenException;
import com.nour.SpringBootMovieReservationSystem.CustomException.InvalidSeatException;
import com.nour.SpringBootMovieReservationSystem.CustomException.MovieNotFoundException;
import com.nour.SpringBootMovieReservationSystem.CustomException.PasswordMismatchException;
import com.nour.SpringBootMovieReservationSystem.CustomException.ReservationNotCancellableException;
import com.nour.SpringBootMovieReservationSystem.CustomException.ReservationNotFoundException;
import com.nour.SpringBootMovieReservationSystem.CustomException.SeatOccupiedException;
import com.nour.SpringBootMovieReservationSystem.CustomException.ShowtimeNotFoundException;
import com.nour.SpringBootMovieReservationSystem.CustomException.UnauthenticatedUserException;
import com.nour.SpringBootMovieReservationSystem.CustomException.UserAlreadyExistsException;
import com.nour.SpringBootMovieReservationSystem.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private final RefreshCookies refreshCookies;

        private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        public GlobalExceptionHandler(RefreshCookies refreshCookies) {
                this.refreshCookies = refreshCookies;
        }

        @ExceptionHandler(HallNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleHallNotFound(HallNotFoundException exc, HttpServletRequest request) {
                ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                                HttpStatus.NOT_FOUND.getReasonPhrase(),
                                exc.getMessage(), request.getRequestURI(), LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        @ExceptionHandler(MovieNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleMovieNotFound(MovieNotFoundException exc,
                        HttpServletRequest request) {
                ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                                HttpStatus.NOT_FOUND.getReasonPhrase(),
                                exc.getMessage(), request.getRequestURI(), LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        @ExceptionHandler(ShowtimeNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleShowtimeNotFound(ShowtimeNotFoundException exc,
                        HttpServletRequest request) {
                ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                                HttpStatus.NOT_FOUND.getReasonPhrase(),
                                exc.getMessage(), request.getRequestURI(), LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ErrorResponse> handleIncorrectCredentials(BadCredentialsException exc,
                        HttpServletRequest request) {
                ErrorResponse error = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(),
                                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                                "Invalid username or password", request.getRequestURI(), LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        @ExceptionHandler(UnauthenticatedUserException.class)
        public ResponseEntity<ErrorResponse> handleUnauthenticatedUser(UnauthenticatedUserException exc,
                        HttpServletRequest request) {
                ErrorResponse error = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(),
                                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                                exc.getMessage(), request.getRequestURI(), LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        @ExceptionHandler(InvalidRefreshTokenException.class)
        public ResponseEntity<ErrorResponse> handleInvalidRefreshToken(InvalidRefreshTokenException exc,
                        HttpServletRequest request) {
                ErrorResponse error = new ErrorResponse(
                                HttpStatus.UNAUTHORIZED.value(),
                                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                                "Invalid session",
                                request.getRequestURI(),
                                LocalDateTime.now());

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .header(HttpHeaders.SET_COOKIE, refreshCookies.clear().toString())
                                .body(error);
        }

        @ExceptionHandler(UserNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleAuthenticatedUserNotFound(UserNotFoundException exc,
                        HttpServletRequest request) {
                ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                "Something went wrong", request.getRequestURI(), LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }

        @ExceptionHandler(ReservationNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleReservationNotFound(ReservationNotFoundException exc,
                        HttpServletRequest request) {
                ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                                HttpStatus.NOT_FOUND.getReasonPhrase(),
                                exc.getMessage(), request.getRequestURI(), LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        @ExceptionHandler(SeatOccupiedException.class)
        public ResponseEntity<ErrorResponse> handleSeatOccupied(SeatOccupiedException exc,
                        HttpServletRequest request) {
                ErrorResponse error = new ErrorResponse(HttpStatus.CONFLICT.value(),
                                HttpStatus.CONFLICT.getReasonPhrase(),
                                exc.getMessage(), request.getRequestURI(), LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        @ExceptionHandler(HallAlreadyOccupiedException.class)
        public ResponseEntity<ErrorResponse> handleHallOccupied(HallAlreadyOccupiedException exc,
                        HttpServletRequest request) {
                ErrorResponse error = new ErrorResponse(HttpStatus.CONFLICT.value(),
                                HttpStatus.CONFLICT.getReasonPhrase(),
                                exc.getMessage(), request.getRequestURI(), LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        @ExceptionHandler(InvalidSeatException.class)
        public ResponseEntity<ErrorResponse> handleIncorrectReservationSeats(InvalidSeatException exc,
                        HttpServletRequest request) {
                ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                exc.getMessage(), request.getRequestURI(), LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        @ExceptionHandler(ReservationNotCancellableException.class)
        public ResponseEntity<ErrorResponse> handleNotCancellable(ReservationNotCancellableException exc,
                        HttpServletRequest request) {
                ErrorResponse error = new ErrorResponse(HttpStatus.CONFLICT.value(),
                                HttpStatus.CONFLICT.getReasonPhrase(), exc.getMessage(),
                                request.getRequestURI(), LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleRequestValidation(MethodArgumentNotValidException exc,
                        HttpServletRequest request) {
                String message = exc.getBindingResult().getFieldErrors().stream()
                                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                                .collect(Collectors.joining(", "));
                ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                message, request.getRequestURI(), LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> handleMissingOrUnreadableBody(HttpMessageNotReadableException exc,
                        HttpServletRequest request) {
                ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                "Request body contains missing, invalid or unknown properties", request.getRequestURI(),
                                LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        // needed to catch thrown exc when validation annot. present in parameters of
        // method this gets thrown over MethodArgumentNotValid
        @ExceptionHandler(HandlerMethodValidationException.class)
        public ResponseEntity<ErrorResponse> handleMethodValidation(HandlerMethodValidationException exc,
                        HttpServletRequest request) {
                String message = exc.getAllErrors().stream()
                                .map(error -> error.getDefaultMessage()).collect(Collectors.joining(", "));
                ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                message, request.getRequestURI(), LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        // ex. get req to admin/movies/ not defined has trailing /
        @ExceptionHandler(NoResourceFoundException.class)
        public ResponseEntity<ErrorResponse> handleUndefinedMappings(NoResourceFoundException exc,
                        HttpServletRequest request) {
                ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                                HttpStatus.NOT_FOUND.getReasonPhrase(),
                                "Resource not found", request.getRequestURI(), LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        // entering in path variable letters
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ErrorResponse> handleIncorrectDataType(MethodArgumentTypeMismatchException exc,
                        HttpServletRequest request) {
                ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                "Invalid value for parameter", request.getRequestURI(), LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        @ExceptionHandler(PasswordMismatchException.class)
        public ResponseEntity<ErrorResponse> handleDifferentPasswordAndConfirmPassword(PasswordMismatchException exc,
                        HttpServletRequest request) {
                ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                exc.getMessage(), request.getRequestURI(), LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        @ExceptionHandler(UserAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleSameEmailOrUsernameAtRegister(UserAlreadyExistsException exc,
                        HttpServletRequest request) {
                ErrorResponse error = new ErrorResponse(HttpStatus.CONFLICT.value(),
                                HttpStatus.CONFLICT.getReasonPhrase(),
                                exc.getMessage(), request.getRequestURI(), LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ErrorResponse> handleDataIntegrityViolationInDB(DataIntegrityViolationException exc,
                        HttpServletRequest request) {
                log.warn("Data integrity violation on {}", request.getRequestURI(), exc);
                ErrorResponse error = new ErrorResponse(HttpStatus.CONFLICT.value(),
                                HttpStatus.CONFLICT.getReasonPhrase(),
                                "Request conflicts with existing data", request.getRequestURI(), LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<ErrorResponse> handleAuthFailure(AuthenticationException exc,
                        HttpServletRequest request) {
                ErrorResponse error = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(),
                                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                                "Invalid username or password", request.getRequestURI(), LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        // includes disabled user exc
        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException exc,
                        HttpServletRequest request) {
                ErrorResponse error = new ErrorResponse(HttpStatus.FORBIDDEN.value(),
                                HttpStatus.FORBIDDEN.getReasonPhrase(),
                                "Access denied", request.getRequestURI(), LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleUnexpected(Exception exc, HttpServletRequest request) {
                // Spring MVC's own exceptions (405 wrong method, 415 wrong content type,
                // missing
                // parameter...) already know their status, so keep it instead of turning it
                // into a 500
                if (exc instanceof org.springframework.web.ErrorResponse mvc
                                && mvc.getStatusCode().is4xxClientError()) {
                        HttpStatus status = HttpStatus.valueOf(mvc.getStatusCode().value());
                        ErrorResponse error = new ErrorResponse(status.value(), status.getReasonPhrase(),
                                        status.getReasonPhrase(), request.getRequestURI(), LocalDateTime.now());
                        return ResponseEntity.status(status).headers(mvc.getHeaders()).body(error);
                }

                log.error("Unhandled exception on {} {}", request.getMethod(), request.getRequestURI(), exc);
                ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                "Something went wrong", request.getRequestURI(), LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
}
