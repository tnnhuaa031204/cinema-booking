package com.cinema.showtime.config;

import com.cinema.showtime.dto.CinemaRequest;
import com.cinema.showtime.dto.RoomRequest;
import com.cinema.showtime.entity.Cinema;
import com.cinema.showtime.repository.CinemaRepository;
import com.cinema.showtime.service.CatalogService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedCinema(CinemaRepository repo, CatalogService service) {
        return args -> {
            if (repo.count() > 0) return;
            Cinema c = service.createCinema(new CinemaRequest("CGV Vincom Hà Nội", "191 Bà Triệu, Hà Nội"));
            service.createRoom(new RoomRequest(c.getId(), "Phòng 1", 5, 8));
            service.createRoom(new RoomRequest(c.getId(), "Phòng 2", 6, 10));
        };
    }
}