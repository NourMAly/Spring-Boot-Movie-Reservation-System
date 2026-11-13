package com.nour.SpringBootMovieReservationSystem.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record ShowtimeResponse(int id,
        Integer hallNumber,
        Integer movieID,
        LocalDate showDate,
        LocalTime showTime,
        BigDecimal price) {
}
