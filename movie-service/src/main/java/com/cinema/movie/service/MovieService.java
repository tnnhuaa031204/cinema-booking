package com.cinema.movie.service;

import com.cinema.movie.dto.MovieRequest;
import com.cinema.movie.dto.MovieResponse;
import com.cinema.movie.entity.Movie;
import com.cinema.movie.entity.MovieStatus;
import com.cinema.movie.repository.MovieRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MovieService {
    private final MovieRepository repo;

    public MovieService(MovieRepository repo) {
        this.repo = repo;
    }

    public List<MovieResponse> list(String keyword) {
        List<Movie> movies = (keyword == null || keyword.isBlank())
                ? repo.findByStatus(MovieStatus.ACTIVE)
                : repo.findByStatusAndTitleContainingIgnoreCase(MovieStatus.ACTIVE, keyword.trim());
        return movies.stream().map(this::toResponse).toList();
    }

    public MovieResponse get(Long id) {
        return toResponse(findActive(id));
    }

    public MovieResponse create(MovieRequest req) {
        Movie m = new Movie();
        apply(m, req);
        m.setStatus(MovieStatus.ACTIVE);
        return toResponse(repo.save(m));
    }

    public MovieResponse update(Long id, MovieRequest req) {
        Movie m = findActive(id);
        apply(m, req);
        return toResponse(repo.save(m));
    }

    // Soft delete: đổi status, không xóa dòng (vì suất chiếu có thể đang tham chiếu)
    public void delete(Long id) {
        Movie m = findActive(id);
        m.setStatus(MovieStatus.INACTIVE);
        repo.save(m);
    }

    private Movie findActive(Long id) {
        return repo.findByIdAndStatus(id, MovieStatus.ACTIVE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phim"));
    }

    private void apply(Movie m, MovieRequest r) {
        m.setTitle(r.title().trim());
        m.setDescription(r.description());
        m.setDurationMinutes(r.durationMinutes());
        m.setGenre(r.genre());
        m.setPosterUrl(r.posterUrl());
        m.setReleaseDate(r.releaseDate());
    }

    private MovieResponse toResponse(Movie m) {
        return new MovieResponse(m.getId(), m.getTitle(), m.getDescription(), m.getDurationMinutes(),
                m.getGenre(), m.getPosterUrl(), m.getReleaseDate(), m.getStatus().name());
    }
}