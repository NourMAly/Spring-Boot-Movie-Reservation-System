package com.nour.SpringBootMovieReservationSystem.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nour.SpringBootMovieReservationSystem.dao.HallDAO;
import com.nour.SpringBootMovieReservationSystem.entity.Hall;

@Service
public class HallServiceImpl implements HallService {
    private HallDAO hallDao;

    public HallServiceImpl(HallDAO hallDAO) {
        this.hallDao = hallDAO;
    }

    @Override
    public Hall addHall(Hall hall) {
        int seatsPerRow = 5;
        List<String> seats = new ArrayList<>();

        for (int i = 0; i < hall.getNumberOfSeats(); i++) {

            int row = i / seatsPerRow + 1;
            char column = (char) ('A' + (i % seatsPerRow));

            seats.add(row + String.valueOf(column));
        }

        hall.setSeats(seats);
        try {
            hallDao.addHall(hall);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return hall;
    }

    @Override
    public void deleteHall(Hall hall) {
        try {
            hallDao.deleteHall(hall);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Override
    public Hall editHall(Hall hall) {
        int seatsPerRow = 5;
        List<String> seats = new ArrayList<>();

        for (int i = 0; i < hall.getNumberOfSeats(); i++) {

            int row = i / seatsPerRow + 1;
            char column = (char) ('A' + (i % seatsPerRow));

            seats.add(row + String.valueOf(column));
        }

        hall.setSeats(seats);
        return hallDao.editHall(hall);
    }

    @Override
    public List<Hall> findAllHalls() {
        try {
            return hallDao.findAllHalls();
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Hall findHallById(int id) {
        try {
            return hallDao.findHallById(id);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return null;
    }
}
