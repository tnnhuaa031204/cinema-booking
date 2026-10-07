package com.cinema.showtime.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "seats",
        uniqueConstraints = @UniqueConstraint(columnNames = {"roomId", "rowLabel", "seatNumber"}))
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long roomId;
    @Column(nullable = false, length = 5)
    private String rowLabel;
    @Column(nullable = false)
    private Integer seatNumber;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeatType seatType = SeatType.NORMAL;

    public String label() { return rowLabel + seatNumber; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public String getRowLabel() { return rowLabel; }
    public void setRowLabel(String rowLabel) { this.rowLabel = rowLabel; }
    public Integer getSeatNumber() { return seatNumber; }
    public void setSeatNumber(Integer seatNumber) { this.seatNumber = seatNumber; }
    public SeatType getSeatType() { return seatType; }
    public void setSeatType(SeatType seatType) { this.seatType = seatType; }
}