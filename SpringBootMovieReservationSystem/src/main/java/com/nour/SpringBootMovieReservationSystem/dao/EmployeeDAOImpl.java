package com.nour.SpringBootMovieReservationSystem.dao;

import org.springframework.stereotype.Repository;

import com.nour.SpringBootMovieReservationSystem.entity.Employee;

import jakarta.persistence.EntityManager;

@Repository
public class EmployeeDAOImpl implements EmployeeDAO {

    private EntityManager entityManager;

    public EmployeeDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Employee findEmployeeById(int id) {
        Employee employee = entityManager.find(Employee.class, id);
        return employee;
    }

}
