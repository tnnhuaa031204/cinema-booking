package com.cinema.movie.controller;

import com.cinema.movie.dto.MovieResponse;
import com.cinema.movie.service.MovieService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/internal/movies")
public class InternalMovieController {
    private final MovieService service;
    private final String internalToken;

    public InternalMovieController(MovieService service, @Value("${internal.token}") String internalToken) {
        this.service = service;
        this.internalToken = internalToken;
    }

    @GetMapping("/{id}")
    public MovieResponse get(@PathVariable Long id,
                             @RequestHeader(value = "X-Internal-Token", required = false) String token) {
        if (!internalToken.equals(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid internal token");
        }
        return service.get(id);
    }
}