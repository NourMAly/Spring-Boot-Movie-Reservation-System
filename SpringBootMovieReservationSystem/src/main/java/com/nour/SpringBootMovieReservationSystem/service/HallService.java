package com.nour.SpringBootMovieReservationSystem.service;

import java.util.List;

import com.nour.SpringBootMovieReservationSystem.entity.Hall;

public interface HallService {
    public Hall findHallById(int id);

    public Hall addHall(Hall hall);

    public void deleteHall(Hall hall);

    public Hall editHall(Hall hall);

    public List<Hall> findAllHalls();
}
