package com.nour.SpringBootMovieReservationSystem.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nour.SpringBootMovieReservationSystem.apiservice.ApiHallService;
import com.nour.SpringBootMovieReservationSystem.apiservice.ApiMovieService;
import com.nour.SpringBootMovieReservationSystem.apiservice.ApiReservationService;
import com.nour.SpringBootMovieReservationSystem.apiservice.ApiShowtimeService;
import com.nour.SpringBootMovieReservationSystem.dto.HallRequest;
import com.nour.SpringBootMovieReservationSystem.dto.HallResponse;
import com.nour.SpringBootMovieReservationSystem.dto.MovieRequest;
import com.nour.SpringBootMovieReservationSystem.dto.MovieResponse;
import com.nour.SpringBootMovieReservationSystem.dto.ReservationResponse;
import com.nour.SpringBootMovieReservationSystem.dto.ShowtimeRequest;
import com.nour.SpringBootMovieReservationSystem.dto.ShowtimeResponse;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("api/v1/admin")
public class AdminRestController {

    private static final String UUID_RE = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    private ApiHallService hallService;
    private ApiMovieService movieService;
    private ApiShowtimeService showtimeService;
    private ApiReservationService apiReservationService;

    public AdminRestController(ApiHallService hallService, ApiMovieService movieService,
            ApiShowtimeService showtimeService, ApiReservationService apiReservationService) {
        this.hallService = hallService;
        this.movieService = movieService;
        this.showtimeService = showtimeService;
        this.apiReservationService = apiReservationService;
    }

    @GetMapping("/halls")
    public List<HallResponse> getHalls() {
        List<HallResponse> responses = hallService.findAllHalls();
        return responses;
    }

    @GetMapping("/halls/{id}")
    public HallResponse getHallById(
            @PathVariable @Min(value = 1, message = "ID must be greater than 0") Integer id) {
        System.out.println("CONTROLLER EXECUTED: " + id);
        return hallService.findHallById(id);
    }

    @PostMapping("/halls")
    public ResponseEntity<HallResponse> addHall(@Valid @RequestBody HallRequest hallRequest) {
        HallResponse response = hallService.addHall(hallRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/halls/{id}")
    public ResponseEntity<HallResponse> editHall(
            @Min(value = 1, message = "Hall ID must be greater than 0") @PathVariable int id,
            @Valid @RequestBody HallRequest hallRequest) {
        return ResponseEntity.ok(hallService.editHall(id, hallRequest));
    }

    @DeleteMapping("/halls/{id}")
    public ResponseEntity<Void> deleteHall(
            @PathVariable @Min(value = 1, message = "Hall ID must be greater than 0") Integer id) {
        hallService.deleteHall(id);
        return ResponseEntity.noContent().build();
    }

    // @GetMapping("/movies")
    // public List<MovieResponse> getAllMovies() {
    // return movieService.findAllMovies();
    // }

    @GetMapping("/movies/{id}")
    public MovieResponse getMovieById(
            @PathVariable @Min(value = 1, message = "Movie ID must be greater than 0") Integer id) {
        return movieService.findMovieById(id);
    }

    @GetMapping("/movies")
    public List<MovieResponse> getMovieByName(@RequestParam(value = "name", required = false) String movieName) {
        if (movieName == null)
            return movieService.findAllMovies();
        return movieService.getMovieByName(movieName);
    }

    @PostMapping("/movies")
    public ResponseEntity<MovieResponse> addMovie(@Valid @RequestBody MovieRequest movieRequest) {
        MovieResponse response = movieService.addMovie(movieRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/movies/{id}")
    public ResponseEntity<MovieResponse> editMovie(
            @Min(value = 1, message = "Movie ID must be greater than 0") @PathVariable("id") int id,
            @Valid @RequestBody MovieRequest movieRequest) {
        return ResponseEntity.ok(movieService.editMovie(id, movieRequest));
    }

    @DeleteMapping("/movies/{id}")
    public ResponseEntity<Void> deleteMovie(
            @PathVariable @Min(value = 1, message = "Movie ID must be greater than 0") Integer id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/showtimes")
    public List<ShowtimeResponse> getAllShowtimes() {
        return showtimeService.getAllShowtimes();
    }

    @GetMapping("/showtimes/{id}")
    public ShowtimeResponse getShowtimeById(
            @Min(value = 1, message = "Showtime id must be greater than 0") @PathVariable Integer id) {
        return showtimeService.getShowtimeById(id);
    }

    @PostMapping("/showtimes")
    public ResponseEntity<ShowtimeResponse> addShowtime(
            @Valid @RequestBody ShowtimeRequest showtimeRequest) {
        ShowtimeResponse response = showtimeService.addShowtime(showtimeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/showtimes/{id}")
    public ResponseEntity<ShowtimeResponse> editShowtime(
            @Min(value = 1, message = "Showtime ID must be greater than 0") @PathVariable Integer id,
            @Valid @RequestBody ShowtimeRequest showtimeRequest) {
        return ResponseEntity.ok(showtimeService.editShowtime(id, showtimeRequest));
    }

    @DeleteMapping("/showtimes/{id}")
    public ResponseEntity<Void> deleteShowtime(
            @Min(value = 1, message = "Showtime ID must be greater than 0") @PathVariable Integer id) {
        showtimeService.deleteShowtime(id);
        return ResponseEntity.noContent().build();

    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        return ResponseEntity.ok(apiReservationService.findAllReservationsEmployee());
    }

    // Employee, in your admin controller under /api/v1/admin/reservations
    @DeleteMapping("/reservations/{bookingRef}")
    public ResponseEntity<Void> delete(
            @PathVariable("bookingRef") @Pattern(regexp = UUID_RE, message = "Invalid reservation reference") String bookingRef) {
        apiReservationService.deleteReservationAsEmployee(bookingRef);
        return ResponseEntity.noContent().build();
    }

}
