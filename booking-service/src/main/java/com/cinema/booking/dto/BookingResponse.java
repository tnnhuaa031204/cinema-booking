package com.cinema.booking.dto;

import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
        Long id, Long showtimeId, String movieTitle, String cinemaName, String roomName,
        LocalDateTime startTime, List<SeatItem> seats, Long totalPrice,
        String status, LocalDateTime holdExpiresAt, LocalDateTime createdAt) {
}