package com.nour.SpringBootMovieReservationSystem.apiservice;

import java.util.List;

import com.nour.SpringBootMovieReservationSystem.dto.HallRequest;
import com.nour.SpringBootMovieReservationSystem.dto.HallResponse;

public interface ApiHallService {
    public HallResponse addHall(HallRequest hallRequest);

    public List<HallResponse> findAllHalls();

    public HallResponse findHallById(int id);

    public HallResponse editHall(int id, HallRequest hallRequest);

    public void deleteHall(int id);
}
