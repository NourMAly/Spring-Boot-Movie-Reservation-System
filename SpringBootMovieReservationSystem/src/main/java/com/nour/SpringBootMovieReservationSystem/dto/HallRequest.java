package com.nour.SpringBootMovieReservationSystem.dto;

import java.util.List;

public record HallRequest(int numberOfSeats, List<String> seats) {

}
