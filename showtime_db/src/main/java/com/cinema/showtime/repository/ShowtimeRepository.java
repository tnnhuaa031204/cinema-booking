package com.cinema.showtime.repository;

import com.cinema.showtime.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    List<Showtime> findByMovieIdAndStartTimeAfterOrderByStartTime(Long movieId, LocalDateTime after);

    List<Showtime> findByStartTimeAfterOrderByStartTime(LocalDateTime after);

    // Đếm suất chiếu trùng giờ trong cùng phòng
    @Query("select count(s) from Showtime s where s.roomId = :roomId and s.startTime < :end and s.endTime > :start")
    long countOverlap(@Param("roomId") Long roomId,
                      @Param("start") LocalDateTime start,
                      @Param("end") LocalDateTime end);
}