package com.cinema.booking.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "booking_seats", indexes = @Index(name = "idx_bs_booking", columnList = "bookingId"))
public class BookingSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long bookingId;
    @Column(nullable = false)
    private Long seatId;
    private String label;
    private String seatType;
    private Long price;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public Long getSeatId() { return seatId; }
    public void setSeatId(Long seatId) { this.seatId = seatId; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public String getSeatType() { return seatType; }
    public void setSeatType(String seatType) { this.seatType = seatType; }
    public Long getPrice() { return price; }
    public void setPrice(Long price) { this.price = price; }
}