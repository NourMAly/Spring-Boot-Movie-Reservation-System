package com.nour.SpringBootMovieReservationSystem.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;
    @Column(name = "seat_label", nullable = false, length = 5)
    private String seatLabel;

    @Column(name = "booking_ref", nullable = false, updatable = false, length = 36)
    private String bookingRef;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal price;

    public Reservation() {
    }

    public Reservation(User user, Showtime showtime, String seatLabel) {
        this.user = user;
        this.showtime = showtime;
        this.seatLabel = seatLabel;
    }

    public Reservation(User user, Showtime showtime, String seatLabel, String bookingRef) {
        this.user = user;
        this.showtime = showtime;
        this.seatLabel = seatLabel;
        this.bookingRef = bookingRef;
    }

    public int getId() {
        return id;
    }

    public Reservation(User user, Showtime showtime, String seatLabel, String bookingRef, BigDecimal price) {
        this.user = user;
        this.showtime = showtime;
        this.seatLabel = seatLabel;
        this.bookingRef = bookingRef;
        this.price = price;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Showtime getShowtime() {
        return showtime;
    }

    public void setShowtime(Showtime showtime) {
        this.showtime = showtime;
    }

    public String getSeatLabel() {
        return seatLabel;
    }

    public void setSeatLabel(String seatLabel) {
        this.seatLabel = seatLabel;
    }

    public String getBookingRef() {
        return bookingRef;
    }

    @Override
    public String toString() {
        return "Reservation [id=" + id
                + ", userId=" + (user != null ? user.getId() : null)
                + ", showtimeId=" + (showtime != null ? showtime.getId() : null)
                + ", seatLabel=" + seatLabel
                + ", bookingRef=" + bookingRef + "]";
    }

}
