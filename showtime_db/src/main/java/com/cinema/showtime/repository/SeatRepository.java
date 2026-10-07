package com.cinema.showtime.repository;

import com.cinema.showtime.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByRoomIdOrderByRowLabelAscSeatNumberAsc(Long roomId);
}