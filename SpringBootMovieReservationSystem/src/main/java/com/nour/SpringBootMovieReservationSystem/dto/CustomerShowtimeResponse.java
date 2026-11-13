package com.nour.SpringBootMovieReservationSystem.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record CustomerShowtimeResponse(Integer hallNumber,
        LocalDate showDate,
        LocalTime showTime,
        BigDecimal price) {

}
