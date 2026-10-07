package com.cinema.movie.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    private String genre;

    @Column(name = "poster_url", length = 500)
    private String posterUrl;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MovieStatus status = MovieStatus.ACTIVE;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }
    public MovieStatus getStatus() { return status; }
    public void setStatus(MovieStatus status) { this.status = status; }
}