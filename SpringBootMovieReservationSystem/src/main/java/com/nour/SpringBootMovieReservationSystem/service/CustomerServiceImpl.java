package com.nour.SpringBootMovieReservationSystem.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nour.SpringBootMovieReservationSystem.dao.CustomerDAO;
import com.nour.SpringBootMovieReservationSystem.entity.Customer;

@Service
public class CustomerServiceImpl implements CustomerService {

    private CustomerDAO customerDAO;

    public CustomerServiceImpl(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    @Override
    public void addCustomer(Customer customer) {
        customerDAO.addCustomer(customer);
    }

    @Override
    public void deleteCustomer(Customer customer) {
        customerDAO.deleteCustomer(customer);
    }

    @Override
    public List<Customer> findAllCustomers() {
        return customerDAO.findAllCustomers();
    }

    @Override
    public Customer findCustomerById(int id) {
        return customerDAO.findCustomerById(id);
    }

    @Override
    public Customer findCustomerByUsername(String username) {
        return customerDAO.findCustomerByUsername(username);
    }

}
