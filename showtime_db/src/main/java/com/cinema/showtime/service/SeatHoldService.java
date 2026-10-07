package com.cinema.showtime.service;

import com.cinema.showtime.dto.*;
import com.cinema.showtime.entity.*;
import com.cinema.showtime.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class SeatHoldService {
    private final ShowtimeRepository showtimeRepo;
    private final ShowtimeSeatRepository showtimeSeatRepo;
    private final SeatRepository seatRepo;
    private final RoomRepository roomRepo;
    private final CinemaRepository cinemaRepo;
    private final long holdSeconds;

    public SeatHoldService(ShowtimeRepository showtimeRepo, ShowtimeSeatRepository showtimeSeatRepo,
                           SeatRepository seatRepo, RoomRepository roomRepo, CinemaRepository cinemaRepo,
                           @Value("${hold.seconds}") long holdSeconds) {
        this.showtimeRepo = showtimeRepo;
        this.showtimeSeatRepo = showtimeSeatRepo;
        this.seatRepo = seatRepo;
        this.roomRepo = roomRepo;
        this.cinemaRepo = cinemaRepo;
        this.holdSeconds = holdSeconds;
    }

    /** All-or-nothing: giữ đủ tất cả ghế hoặc không giữ ghế nào (rollback + 409). */
    @Transactional
    public HoldResponse hold(Long showtimeId, HoldRequest req) {
        Showtime st = showtimeRepo.findById(showtimeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy suất chiếu"));
        LocalDateTime now = LocalDateTime.now();
        if (!st.getStartTime().isAfter(now)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Suất chiếu đã bắt đầu");
        }

        List<Long> seatIds = req.seatIds().stream().distinct().toList();
        LocalDateTime expiresAt = now.plusSeconds(holdSeconds);

        int updated = showtimeSeatRepo.hold(showtimeId, seatIds, req.userId(), req.bookingId(), expiresAt, now);
        if (updated != seatIds.size()) {
            // RuntimeException => @Transactional tự rollback, không giữ được "một nửa" số ghế
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Có ghế đã bị người khác giữ hoặc đã bán");
        }

        List<HeldSeat> held = seatRepo.findAllById(seatIds).stream()
                .sorted(Comparator.comparing(Seat::getRowLabel).thenComparing(Seat::getSeatNumber))
                .map(s -> new HeldSeat(s.getId(), s.label(), s.getSeatType().name(),
                        CatalogService.priceOf(st, s)))
                .toList();
        long total = held.stream().mapToLong(HeldSeat::price).sum();

        Room room = roomRepo.findById(st.getRoomId()).orElseThrow();
        String cinemaName = cinemaRepo.findById(room.getCinemaId()).map(Cinema::getName).orElse("");
        return new HoldResponse(st.getId(), st.getMovieTitle(), cinemaName, room.getName(),
                st.getStartTime(), held, total, expiresAt);
    }

    /** Chỉ confirm được nếu ghế vẫn đang HOLDING của đúng booking và chưa hết hạn. */
    @Transactional
    public void confirm(Long bookingId) {
        List<ShowtimeSeat> rows = showtimeSeatRepo.findByBookingId(bookingId);
        if (rows.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.GONE, "Không còn ghế nào được giữ cho booking này");
        }
        if (rows.stream().allMatch(r -> r.getStatus() == SeatStatus.BOOKED)) {
            return; // gọi lặp lại: coi như đã thành công
        }
        int updated = showtimeSeatRepo.confirm(bookingId, LocalDateTime.now());
        if (updated != rows.size()) {
            throw new ResponseStatusException(HttpStatus.GONE, "Thời gian giữ ghế đã hết hạn"); // rollback
        }
    }

    /** Nhả ghế theo booking: dùng khi hủy vé, hold lỗi hoặc booking hết hạn. */
    @Transactional
    public int release(Long bookingId) {
        return showtimeSeatRepo.release(bookingId);
    }
}