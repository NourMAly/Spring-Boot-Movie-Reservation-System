package com.nour.SpringBootMovieReservationSystem.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ReservationResponse(
        String bookingReference,
        String movieName,
        LocalDate showDate,
        LocalTime showTime,
        Integer runTimeinMinutes,
        List<String> seats,
        BigDecimal totalPrice) {

}
