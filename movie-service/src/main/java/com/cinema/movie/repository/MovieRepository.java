package com.cinema.movie.repository;

import com.cinema.movie.entity.Movie;
import com.cinema.movie.entity.MovieStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByStatus(MovieStatus status);
    List<Movie> findByStatusAndTitleContainingIgnoreCase(MovieStatus status, String keyword);
    Optional<Movie> findByIdAndStatus(Long id, MovieStatus status);
    boolean existsByTitle(String title);
}