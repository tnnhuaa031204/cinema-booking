package com.cinema.showtime.repository;

import com.cinema.showtime.entity.ShowtimeSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ShowtimeSeatRepository extends JpaRepository<ShowtimeSeat, Long> {

    List<ShowtimeSeat> findByShowtimeId(Long showtimeId);

    List<ShowtimeSeat> findByBookingId(Long bookingId);

    // Giữ ghế: chỉ ghế AVAILABLE hoặc ghế HOLDING đã hết hạn mới được giữ
    @Modifying(clearAutomatically = true)
    @Query(value = """
            UPDATE showtime_seats
            SET status = 'HOLDING', hold_by = :userId, hold_expires_at = :exp, booking_id = :bookingId
            WHERE showtime_id = :showtimeId
              AND seat_id IN (:seatIds)
              AND (status = 'AVAILABLE' OR (status = 'HOLDING' AND hold_expires_at < :now))
            """, nativeQuery = true)
    int hold(@Param("showtimeId") Long showtimeId,
             @Param("seatIds") List<Long> seatIds,
             @Param("userId") Long userId,
             @Param("bookingId") Long bookingId,
             @Param("exp") LocalDateTime exp,
             @Param("now") LocalDateTime now);

    // Xác nhận: chỉ ghế còn đang HOLDING của đúng booking và chưa hết hạn
    @Modifying(clearAutomatically = true)
    @Query(value = """
            UPDATE showtime_seats
            SET status = 'BOOKED', hold_expires_at = NULL
            WHERE booking_id = :bookingId AND status = 'HOLDING' AND hold_expires_at > :now
            """, nativeQuery = true)
    int confirm(@Param("bookingId") Long bookingId, @Param("now") LocalDateTime now);

    // Nhả ghế (dùng cho hủy booking, hold lỗi, hết hạn)
    @Modifying(clearAutomatically = true)
    @Query(value = """
            UPDATE showtime_seats
            SET status = 'AVAILABLE', hold_by = NULL, hold_expires_at = NULL, booking_id = NULL
            WHERE booking_id = :bookingId
            """, nativeQuery = true)
    int release(@Param("bookingId") Long bookingId);
}