package com.nour.SpringBootMovieReservationSystem.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @Column(name = "show_date")
    private LocalDate showDate;

    @Column(name = "price")
    private BigDecimal price;

    public Showtime() {
    }

    // public Showtime(Hall hall, LocalTime startTime, Movie movie) {
    // this.hall = hall;
    // this.startTime = startTime;
    // this.movie = movie;
    // }

    public int getId() {
        return id;
    }

    public Showtime(Hall hall, LocalTime startTime, Movie movie, LocalDate showDate, BigDecimal price) {
        this.hall = hall;
        this.startTime = startTime;
        this.movie = movie;
        this.showDate = showDate;
        this.price = price;
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

    public LocalDate getShowDate() {
        return showDate;
    }

    public void setShowDate(LocalDate showDate) {
        this.showDate = showDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Showtime [id=" + id + ", hall=" + hall + ", startTime=" + startTime + ", movie=" + movie + ", showDate="
                + showDate + ", price=" + price + "]";
    }

}
