package com.cinema.booking.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
public class ShowtimeClient {
    private final RestClient client;
    private final String token;

    public ShowtimeClient(@Value("${showtime.url}") String url, @Value("${internal.token}") String token) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2000);
        factory.setReadTimeout(5000);
        this.client = RestClient.builder().baseUrl(url).requestFactory(factory).build();
        this.token = token;
    }

    public HoldResult hold(Long showtimeId, Long bookingId, Long userId, List<Long> seatIds) {
        try {
            return client.post()
                    .uri("/internal/showtimes/{id}/hold", showtimeId)
                    .header("X-Internal-Token", token)
                    .body(new HoldRequest(bookingId, userId, seatIds))
                    .retrieve()
                    .body(HoldResult.class);
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().value() == 409) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Có ghế đã bị người khác giữ hoặc đã bán");
            }
            if (e.getStatusCode().value() == 404) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy suất chiếu");
            }
            if (e.getStatusCode().value() == 400) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Yêu cầu giữ ghế không hợp lệ hoặc suất chiếu đã bắt đầu");
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Showtime Service lỗi: " + e.getStatusCode());
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Showtime Service không phản hồi");
        }
    }

    public void confirm(Long bookingId) {
        try {
            client.post()
                    .uri("/internal/showtimes/bookings/{id}/confirm", bookingId)
                    .header("X-Internal-Token", token)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().value() == 410) {
                throw new ResponseStatusException(HttpStatus.GONE, "Thời gian giữ ghế đã hết hạn");
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Showtime Service lỗi: " + e.getStatusCode());
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Showtime Service không phản hồi");
        }
    }

    public void release(Long bookingId) {
        try {
            client.post()
                    .uri("/internal/showtimes/bookings/{id}/release", bookingId)
                    .header("X-Internal-Token", token)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpStatusCodeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Showtime Service lỗi: " + e.getStatusCode());
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Showtime Service không phản hồi");
        }
    }
}