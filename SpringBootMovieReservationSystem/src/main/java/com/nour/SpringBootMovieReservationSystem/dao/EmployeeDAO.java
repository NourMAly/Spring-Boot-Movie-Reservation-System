package com.nour.SpringBootMovieReservationSystem.dao;

import com.nour.SpringBootMovieReservationSystem.entity.Employee;

public interface EmployeeDAO {

    public Employee findEmployeeById(int id);

}
