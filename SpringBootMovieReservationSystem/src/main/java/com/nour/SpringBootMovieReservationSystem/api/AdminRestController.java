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
import org.springframework.web.bind.annotation.RestController;

import com.nour.SpringBootMovieReservationSystem.apiservice.ApiHallService;
import com.nour.SpringBootMovieReservationSystem.apiservice.ApiMovieService;
import com.nour.SpringBootMovieReservationSystem.dto.HallRequest;
import com.nour.SpringBootMovieReservationSystem.dto.HallResponse;
import com.nour.SpringBootMovieReservationSystem.dto.MovieResponse;
import com.nour.SpringBootMovieReservationSystem.entity.Movie;
import com.nour.SpringBootMovieReservationSystem.service.MovieService;
import com.nour.SpringBootMovieReservationSystem.service.ShowtimeService;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("api/v1/admin")
public class AdminRestController {

    private ApiHallService hallService;
    private ApiMovieService movieService;
    private ShowtimeService showtimeService;

    public AdminRestController(ApiHallService hallService, ApiMovieService movieService,
            ShowtimeService showtimeService) {
        this.hallService = hallService;
        this.movieService = movieService;
        this.showtimeService = showtimeService;
    }

    @GetMapping("/halls")
    public List<HallResponse> getHalls() {
        List<HallResponse> responses = hallService.findAllHalls();
        return responses;
    }

    @GetMapping("/halls/{id}")
    public HallResponse getHallById(@PathVariable("id") int id) {
        return hallService.findHallById(id);
    }

    @PostMapping("halls")
    public ResponseEntity<HallResponse> addHall(@RequestBody HallRequest hallRequest) {
        HallResponse response = hallService.addHall(hallRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("halls/{id}")
    public ResponseEntity<HallResponse> editHall(@PathVariable("id") int id,
            @RequestBody HallRequest hallRequest) {
        return ResponseEntity.ok(hallService.editHall(id, hallRequest));
    }

    @DeleteMapping("halls/{id}")
    public ResponseEntity<Void> deleteHall(@PathVariable("id") int id) {
        hallService.deleteHall(id);
        return ResponseEntity.noContent().build();
    }

    // @GetMapping("/movies")
    // public List<MovieResponse> getAllMovies() {
    // List<Movie> movies = movieService.findAllMovies();

    // }

}
