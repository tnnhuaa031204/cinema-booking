package com.cinema.booking.controller;

import com.cinema.booking.dto.BookingResponse;
import com.cinema.booking.dto.CreateBookingRequest;
import com.cinema.booking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> create(@RequestHeader("X-User-Id") Long userId,
                                                  @Valid @RequestBody CreateBookingRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(userId, req));
    }

    @GetMapping("/my")
    public List<BookingResponse> mine(@RequestHeader("X-User-Id") Long userId) {
        return service.listMine(userId);
    }

    @GetMapping("/{id}")
    public BookingResponse get(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id) {
        return service.get(userId, id);
    }

    @PostMapping("/{id}/confirm")
    public BookingResponse confirm(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id) {
        return service.confirm(userId, id);
    }

    @PostMapping("/{id}/cancel")
    public BookingResponse cancel(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id) {
        return service.cancel(userId, id);
    }
}