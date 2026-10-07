package com.cinema.booking.client;

import com.cinema.booking.dto.SeatItem;

import java.time.LocalDateTime;
import java.util.List;

public record HoldResult(
        Long showtimeId, String movieTitle, String cinemaName, String roomName,
        LocalDateTime startTime, List<SeatItem> seats, Long totalPrice, LocalDateTime expiresAt) {
}