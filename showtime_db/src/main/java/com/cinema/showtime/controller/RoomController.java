package com.cinema.showtime.controller;

import com.cinema.showtime.dto.RoomRequest;
import com.cinema.showtime.entity.Room;
import com.cinema.showtime.service.CatalogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final CatalogService service;

    public RoomController(CatalogService service) {
        this.service = service;
    }

    @GetMapping
    public List<Room> list(@RequestParam(required = false) Long cinemaId) {
        return service.listRooms(cinemaId);
    }

    @PostMapping
    public ResponseEntity<Room> create(@Valid @RequestBody RoomRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createRoom(req));
    }
}