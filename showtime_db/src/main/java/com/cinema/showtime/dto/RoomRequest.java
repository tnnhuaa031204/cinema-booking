package com.cinema.showtime.dto;

import jakarta.validation.constraints.*;

public record RoomRequest(
        @NotNull Long cinemaId,
        @NotBlank String name,
        @NotNull @Min(1) @Max(26) Integer rows,
        @NotNull @Min(1) @Max(30) Integer seatsPerRow) {
}