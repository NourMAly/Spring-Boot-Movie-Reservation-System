package com.nour.SpringBootMovieReservationSystem.dto;

import java.util.List;

public record HallResponse(int id,
                int numberOfSeats,
                List<String> seats) {

}
