package com.cinema.showtime.dto;

import java.time.LocalDateTime;

public record ShowtimeResponse(
        Long id, Long movieId, String movieTitle,
        Long roomId, String roomName, String cinemaName,
        LocalDateTime startTime, LocalDateTime endTime, Long basePrice) {
}