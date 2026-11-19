package com.nour.SpringBootMovieReservationSystem.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nour.SpringBootMovieReservationSystem.apiservice.ApiReservationService;
import com.nour.SpringBootMovieReservationSystem.dto.ReservationRequest;
import com.nour.SpringBootMovieReservationSystem.dto.ReservationResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationRestController {

    private static final String UUID_RE = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    private ApiReservationService apiReservationService;

    public ReservationRestController(ApiReservationService apiReservationService) {
        this.apiReservationService = apiReservationService;
    }

    @Operation(summary = "Reserve seats", description = "Reserves one or more seats for a specific showtime. " +
            "Only authenticated users can make reservations.")
    @PostMapping("/reserve")
    public ResponseEntity<ReservationResponse> reserve(@Valid @RequestBody ReservationRequest reservationRequest,
            @AuthenticationPrincipal Jwt jwt) {
        ReservationResponse response = apiReservationService.addReservation(reservationRequest, jwt.getSubject());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/myreservations")
    public List<ReservationResponse> getAllCustomerReservations(@AuthenticationPrincipal Jwt jwt) {
        return apiReservationService.allMyreservations(jwt.getSubject());
    }

    // @DeleteMapping("{id}")
    // public ResponseEntity<Void> deleteMyReservation(
    // @Min(value = 1, message = "Reservation ID must be greater than 0")
    // @PathVariable int id,
    // @AuthenticationPrincipal Jwt jwt) {
    // apiReservationService.deleteMyReservation(id, jwt.getSubject());
    // return ResponseEntity.noContent().build();
    // }

    // Customer
    @DeleteMapping("/{bookingRef}")
    public ResponseEntity<Void> cancel(
            @PathVariable("bookingRef") @Pattern(regexp = UUID_RE, message = "Invalid reservation reference") String bookingRef,
            @AuthenticationPrincipal Jwt jwt) {
        apiReservationService.cancelMyReservation(bookingRef, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }
}
