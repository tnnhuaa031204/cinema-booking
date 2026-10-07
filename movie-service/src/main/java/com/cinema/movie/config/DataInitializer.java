package com.cinema.movie.config;

import com.cinema.movie.entity.Movie;
import com.cinema.movie.repository.MovieRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedMovies(MovieRepository repo) {
        return args -> {
            if (repo.count() > 0) return;
            repo.save(movie("Mai", "Phim tâm lý tình cảm Việt Nam", 131, "Tình cảm", LocalDate.of(2024, 2, 10)));
            repo.save(movie("Đào, Phở và Piano", "Phim chiến tranh lịch sử", 100, "Lịch sử", LocalDate.of(2024, 2, 10)));
            repo.save(movie("Dune: Part Two", "Hành trình của Paul Atreides", 166, "Khoa học viễn tưởng", LocalDate.of(2024, 3, 1)));
            repo.save(movie("Kung Fu Panda 4", "Gấu Po trở lại", 94, "Hoạt hình", LocalDate.of(2024, 3, 8)));
        };
    }

    private Movie movie(String title, String desc, int duration, String genre, LocalDate release) {
        Movie m = new Movie();
        m.setTitle(title);
        m.setDescription(desc);
        m.setDurationMinutes(duration);
        m.setGenre(genre);
        m.setReleaseDate(release);
        return m;
    }
}