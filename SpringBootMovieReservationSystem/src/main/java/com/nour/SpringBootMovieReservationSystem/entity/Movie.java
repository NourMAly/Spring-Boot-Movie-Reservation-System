package com.nour.SpringBootMovieReservationSystem.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "movie")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String movieName;

    @Column(name = "genre")
    private String genre;

    @Column(name = "posterURL")
    private String posterURL;

    @Column(name = "description")

    private String description;

    @Column(name = "runtimeinminutes")
    private String runTimeInMinutes;

    @OneToMany(mappedBy = "movie")
    private List<Showtime> showtimes = new ArrayList<>();

    public Movie() {
    }

    public Movie(String movieName, String genre, String posterURL, String description, String runTimeInMinutes) {
        this.movieName = movieName;
        this.genre = genre;
        this.posterURL = posterURL;
        this.description = description;
        this.runTimeInMinutes = runTimeInMinutes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPosterURL() {
        return posterURL;
    }

    public void setPosterURL(String posterURL) {
        this.posterURL = posterURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRunTimeInMinutes() {
        return runTimeInMinutes;
    }

    public void setRunTimeInMinutes(String runTimeInMinutes) {
        this.runTimeInMinutes = runTimeInMinutes;
    }

    public List<Showtime> getShowtimes() {
        return showtimes;
    }

    public void setShowtimes(List<Showtime> showtimes) {
        this.showtimes = showtimes;
    }

    @Override
    public String toString() {
        return "Movie [id=" + id + ", movieName=" + movieName + ", genre=" + genre + ", posterURL=" + posterURL
                + ", description=" + description + ", runTimeInMinutes=" + runTimeInMinutes + "]";
    }

}
