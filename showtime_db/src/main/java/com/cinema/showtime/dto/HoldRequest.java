package com.cinema.showtime.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public record HoldRequest(
        @NotNull Long bookingId,
        @NotNull Long userId,
        @NotEmpty @Size(max = 8) List<Long> seatIds) {
}