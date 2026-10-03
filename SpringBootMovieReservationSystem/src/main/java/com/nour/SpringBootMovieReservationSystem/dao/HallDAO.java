package com.nour.SpringBootMovieReservationSystem.dao;

import java.util.List;

import com.nour.SpringBootMovieReservationSystem.entity.Hall;

public interface HallDAO {

    public Hall findHallById(int id);

    public void addHall(Hall hall);

    public void deleteHall(Hall hall);

    public Hall editHall(Hall hall);

    public List<Hall> findAllHalls();

}
