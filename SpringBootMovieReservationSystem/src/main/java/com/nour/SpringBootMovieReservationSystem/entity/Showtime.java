package com.nour.SpringBootMovieReservationSystem.entity;

import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "showtime")
public class Showtime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "hall_number")
    private Hall hall;

    @Column(name = "start_time")
    private LocalTime startTime;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    public Showtime() {
    }

    public Showtime(Hall hall, LocalTime startTime, Movie movie) {
        this.hall = hall;
        this.startTime = startTime;
        this.movie = movie;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Hall getHall() {
        return hall;
    }

    public void setHall(Hall hall) {
        this.hall = hall;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    @Override
    public String toString() {
        return "Showtime [id=" + id + ", hall=" + hall + ", starTime=" + startTime + ", movie=" + movie + "]";
    }

}
