package com.cinema.showtime.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class MovieClient {
    private final RestClient client;
    private final String token;

    public MovieClient(@Value("${movie.url}") String url, @Value("${internal.token}") String token) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2000);
        factory.setReadTimeout(3000);
        this.client = RestClient.builder().baseUrl(url).requestFactory(factory).build();
        this.token = token;
    }

    public MovieInfo getMovie(Long id) {
        try {
            return client.get()
                    .uri("/internal/movies/{id}", id)
                    .header("X-Internal-Token", token)
                    .retrieve()
                    .body(MovieInfo.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phim không tồn tại hoặc đã ngừng chiếu");
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Movie Service không phản hồi");
        }
    }
}