package com.nour.SpringBootMovieReservationSystem.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "hall")
public class Hall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "number_of_seats")
    private int numberOfSeats;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "seats", columnDefinition = "json")
    private List<String> seats = new ArrayList<>();

    @OneToMany(mappedBy = "hall")
    private List<Showtime> showtimes = new ArrayList<>();

    public Hall() {
    }

    public Hall(int numberOfSeats, List<String> seats) {
        this.numberOfSeats = numberOfSeats;
        this.seats = seats;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public List<String> getSeats() {
        return seats;
    }

    public void setSeats(List<String> seats) {
        this.seats = seats;
    }

    @Override
    public String toString() {
        return "Hall [id=" + id + ", numberOfSeats=" + numberOfSeats + ", seats=" + seats + "]";
    }

    public List<Showtime> getShowtimes() {
        return showtimes;
    }

    public void setShowtimes(List<Showtime> showtimes) {
        this.showtimes = showtimes;
    }

}
