package com.cinema.showtime.controller;

import com.cinema.showtime.dto.CinemaRequest;
import com.cinema.showtime.entity.Cinema;
import com.cinema.showtime.service.CatalogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cinemas")
public class CinemaController {
    private final CatalogService service;

    public CinemaController(CatalogService service) {
        this.service = service;
    }

    @GetMapping
    public List<Cinema> list() {
        return service.listCinemas();
    }

    @PostMapping
    public ResponseEntity<Cinema> create(@Valid @RequestBody CinemaRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createCinema(req));
    }
}