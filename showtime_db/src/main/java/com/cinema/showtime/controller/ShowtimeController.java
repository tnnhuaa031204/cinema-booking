package com.cinema.showtime.controller;

import com.cinema.showtime.dto.*;
import com.cinema.showtime.service.CatalogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/showtimes")
public class ShowtimeController {
    private final CatalogService service;

    public ShowtimeController(CatalogService service) {
        this.service = service;
    }

    @GetMapping
    public List<ShowtimeResponse> list(@RequestParam(required = false) Long movieId) {
        return service.listShowtimes(movieId);
    }

    @GetMapping("/{id}")
    public ShowtimeResponse get(@PathVariable Long id) {
        return service.getShowtime(id);
    }

    @GetMapping("/{id}/seats")
    public List<SeatView> seats(@PathVariable Long id) {
        return service.getSeats(id);
    }

    @PostMapping
    public ResponseEntity<ShowtimeResponse> create(@Valid @RequestBody ShowtimeRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createShowtime(req));
    }
}