package com.cinema.showtime.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record ShowtimeRequest(
        @NotNull Long movieId,
        @NotNull Long roomId,
        @NotNull LocalDateTime startTime,
        @NotNull @Min(1000) Long basePrice) {
}