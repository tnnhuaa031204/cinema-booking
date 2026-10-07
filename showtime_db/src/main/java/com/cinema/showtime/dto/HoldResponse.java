package com.cinema.showtime.dto;

import java.time.LocalDateTime;
import java.util.List;

public record HoldResponse(
        Long showtimeId, String movieTitle, String cinemaName, String roomName,
        LocalDateTime startTime, List<HeldSeat> seats, Long totalPrice, LocalDateTime expiresAt) {
}