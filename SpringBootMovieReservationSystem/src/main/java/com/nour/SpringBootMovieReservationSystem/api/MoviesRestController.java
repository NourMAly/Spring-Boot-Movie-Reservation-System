package com.nour.SpringBootMovieReservationSystem.api;

import org.springframework.web.bind.annotation.RestController;

import com.nour.SpringBootMovieReservationSystem.apiservice.ApiMoviesShowtimesService;
import com.nour.SpringBootMovieReservationSystem.dto.CustomerMovieResponse;
import com.nour.SpringBootMovieReservationSystem.dto.SeatMapResponse;

import jakarta.validation.constraints.Min;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1")
public class MoviesRestController {

    private ApiMoviesShowtimesService apiMoviesShowtimesService;

    public MoviesRestController(ApiMoviesShowtimesService apiMoviesShowtimesService) {
        this.apiMoviesShowtimesService = apiMoviesShowtimesService;
    }

    // @GetMapping("/movies")
    // public List<CustomerMovieResponse> getAllMovies() {
    // return apiMoviesShowtimesService.getAllMoviesAndShowtimes();
    // }

    // each movie with its upcoming showtimes paginated
    @GetMapping("/movies")
    public Page<CustomerMovieResponse> getMovies(
            @PageableDefault(size = 10, sort = "movieName", direction = Sort.Direction.ASC) Pageable pageable) {
        return apiMoviesShowtimesService.getAllMoviesAndShowtimesPaginated(pageable);
    }

    // specific movie with its upcoming showtimes
    @GetMapping("/movies/{id}")
    public CustomerMovieResponse getAllMovieShowtimes(
            @Min(value = 1, message = "Movie id must be greater than 0") @Min(value = 1, message = "Id must be greater than 0") @PathVariable Integer id) {
        return apiMoviesShowtimesService.getMovieAndItsShowtimes(id);
    }

    @GetMapping("/showtimes/{id}")
    public SeatMapResponse getShowtimeSeatmap(
            @Min(value = 1, message = "Showtime id must be greater than 0") @PathVariable Integer id) {
        return apiMoviesShowtimesService.getSeatMapForShowtime(id);
    }

}
