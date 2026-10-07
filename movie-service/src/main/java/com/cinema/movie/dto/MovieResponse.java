package com.cinema.movie.dto;

import java.time.LocalDate;

public record MovieResponse(
        Long id,
        String title,
        String description,
        Integer durationMinutes,
        String genre,
        String posterUrl,
        LocalDate releaseDate,
        String status) {
}