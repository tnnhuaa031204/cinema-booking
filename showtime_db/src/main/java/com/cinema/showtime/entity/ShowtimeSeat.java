package com.cinema.showtime.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "showtime_seats",
        uniqueConstraints = @UniqueConstraint(columnNames = {"showtimeId", "seatId"}),
        indexes = @Index(name = "idx_booking", columnList = "bookingId"))
public class ShowtimeSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long showtimeId;
    @Column(nullable = false)
    private Long seatId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeatStatus status = SeatStatus.AVAILABLE;
    private Long holdBy;
    private LocalDateTime holdExpiresAt;
    private Long bookingId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getShowtimeId() { return showtimeId; }
    public void setShowtimeId(Long showtimeId) { this.showtimeId = showtimeId; }
    public Long getSeatId() { return seatId; }
    public void setSeatId(Long seatId) { this.seatId = seatId; }
    public SeatStatus getStatus() { return status; }
    public void setStatus(SeatStatus status) { this.status = status; }
    public Long getHoldBy() { return holdBy; }
    public void setHoldBy(Long holdBy) { this.holdBy = holdBy; }
    public LocalDateTime getHoldExpiresAt() { return holdExpiresAt; }
    public void setHoldExpiresAt(LocalDateTime holdExpiresAt) { this.holdExpiresAt = holdExpiresAt; }
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
}