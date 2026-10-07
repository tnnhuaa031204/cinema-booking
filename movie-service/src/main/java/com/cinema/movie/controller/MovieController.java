package com.cinema.movie.controller;

import com.cinema.movie.dto.MovieRequest;
import com.cinema.movie.dto.MovieResponse;
import com.cinema.movie.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Quyền ADMIN cho POST/PUT/DELETE đã được Gateway kiểm tra
@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private final MovieService service;

    public MovieController(MovieService service) {
        this.service = service;
    }

    @GetMapping
    public List<MovieResponse> list(@RequestParam(required = false) String keyword) {
        return service.list(keyword);
    }

    @GetMapping("/{id}")
    public MovieResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public ResponseEntity<MovieResponse> create(@Valid @RequestBody MovieRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }

    @PutMapping("/{id}")
    public MovieResponse update(@PathVariable Long id, @Valid @RequestBody MovieRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}