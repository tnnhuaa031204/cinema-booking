package com.cinema.booking.client;

import java.util.List;

public record HoldRequest(Long bookingId, Long userId, List<Long> seatIds) {
}