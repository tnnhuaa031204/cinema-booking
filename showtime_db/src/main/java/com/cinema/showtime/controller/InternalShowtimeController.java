package com.cinema.showtime.controller;

import com.cinema.showtime.dto.HoldRequest;
import com.cinema.showtime.dto.HoldResponse;
import com.cinema.showtime.service.SeatHoldService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/internal/showtimes")
public class InternalShowtimeController {
    private final SeatHoldService service;
    private final String internalToken;

    public InternalShowtimeController(SeatHoldService service, @Value("${internal.token}") String internalToken) {
        this.service = service;
        this.internalToken = internalToken;
    }

    @PostMapping("/{showtimeId}/hold")
    public HoldResponse hold(@PathVariable Long showtimeId,
                             @Valid @RequestBody HoldRequest req,
                             @RequestHeader(value = "X-Internal-Token", required = false) String token) {
        check(token);
        return service.hold(showtimeId, req);
    }

    @PostMapping("/bookings/{bookingId}/confirm")
    public ResponseEntity<Void> confirm(@PathVariable Long bookingId,
                                        @RequestHeader(value = "X-Internal-Token", required = false) String token) {
        check(token);
        service.confirm(bookingId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bookings/{bookingId}/release")
    public ResponseEntity<Void> release(@PathVariable Long bookingId,
                                        @RequestHeader(value = "X-Internal-Token", required = false) String token) {
        check(token);
        service.release(bookingId);
        return ResponseEntity.noContent().build();
    }

    private void check(String token) {
        if (!internalToken.equals(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid internal token");
        }
    }
}