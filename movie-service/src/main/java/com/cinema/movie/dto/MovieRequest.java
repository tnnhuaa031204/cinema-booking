package com.cinema.movie.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record MovieRequest(
        @NotBlank @Size(max = 255) String title,
        String description,
        @NotNull @Min(1) @Max(600) Integer durationMinutes,
        String genre,
        String posterUrl,
        LocalDate releaseDate) {
}