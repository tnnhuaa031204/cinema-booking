package com.cinema.booking.dto;

import jakarta.validation.constraints.*;
import java.util.List;

// Không có trường giá: giá do server (Showtime) tính
public record CreateBookingRequest(
        @NotNull Long showtimeId,
        @NotEmpty @Size(max = 8) List<Long> seatIds) {
}