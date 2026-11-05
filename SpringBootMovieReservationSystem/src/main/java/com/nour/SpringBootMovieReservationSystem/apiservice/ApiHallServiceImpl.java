package com.nour.SpringBootMovieReservationSystem.apiservice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nour.SpringBootMovieReservationSystem.dao.HallDAO;
import com.nour.SpringBootMovieReservationSystem.dto.HallRequest;
import com.nour.SpringBootMovieReservationSystem.dto.HallResponse;
import com.nour.SpringBootMovieReservationSystem.entity.Hall;

import jakarta.transaction.Transactional;

@Service
public class ApiHallServiceImpl implements ApiHallService {

    private HallDAO hallDao;

    public ApiHallServiceImpl(HallDAO hallDAO) {
        this.hallDao = hallDAO;
    }

    @Override
    public HallResponse addHall(HallRequest hallRequest) {
        Hall hall = new Hall(hallRequest.numberOfSeats(), hallRequest.seats());
        try {
            hallDao.addHall(hall);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        return new HallResponse(hall.getId(), hall.getNumberOfSeats(), hall.getSeats());
    }

    @Override
    public List<HallResponse> findAllHalls() {
        List<Hall> halls = hallDao.findAllHalls();
        if (halls.isEmpty()) {
            throw new RuntimeException("No halls found.");
        }
        List<HallResponse> responses = new ArrayList<>();
        for (Hall h : halls) {
            responses.add(new HallResponse(h.getId(), h.getNumberOfSeats(), h.getSeats()));
        }
        return responses;
    }

    @Override
    public HallResponse findHallById(int id) {
        Hall h = hallDao.findHallById(id);
        if (h == null)
            throw new RuntimeException("Hall not found with id: " + id);
        return new HallResponse(h.getId(), h.getNumberOfSeats(), h.getSeats());
    }

    @Override
    @Transactional
    public HallResponse editHall(int id, HallRequest hallRequest) {
        Hall hall = hallDao.findHallById(id);
        hall.setNumberOfSeats(hallRequest.numberOfSeats());
        hall.setSeats(hallRequest.seats());
        return new HallResponse(hall.getId(), hall.getNumberOfSeats(), hall.getSeats());
    }

    @Override
    @Transactional
    public void deleteHall(int id) {
        Hall h = hallDao.findHallById(id);
        if (h == null)
            throw new RuntimeException("Hall not found with id: " + id);
        hallDao.deleteHall(h);
    }

}
