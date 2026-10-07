package com.cinema.showtime.dto;

public record SeatView(Long seatId, String label, String rowLabel, Integer seatNumber,
                       String type, Long price, String status) {
}