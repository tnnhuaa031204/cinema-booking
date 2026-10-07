package com.cinema.showtime.service;

import com.cinema.showtime.client.MovieClient;
import com.cinema.showtime.client.MovieInfo;
import com.cinema.showtime.dto.*;
import com.cinema.showtime.entity.*;
import com.cinema.showtime.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CatalogService {
    private final CinemaRepository cinemaRepo;
    private final RoomRepository roomRepo;
    private final SeatRepository seatRepo;
    private final ShowtimeRepository showtimeRepo;
    private final ShowtimeSeatRepository showtimeSeatRepo;
    private final MovieClient movieClient;

    public CatalogService(CinemaRepository cinemaRepo, RoomRepository roomRepo, SeatRepository seatRepo,
                          ShowtimeRepository showtimeRepo, ShowtimeSeatRepository showtimeSeatRepo,
                          MovieClient movieClient) {
        this.cinemaRepo = cinemaRepo;
        this.roomRepo = roomRepo;
        this.seatRepo = seatRepo;
        this.showtimeRepo = showtimeRepo;
        this.showtimeSeatRepo = showtimeSeatRepo;
        this.movieClient = movieClient;
    }

    // ---------- Cinema ----------
    public Cinema createCinema(CinemaRequest req) {
        Cinema c = new Cinema();
        c.setName(req.name().trim());
        c.setAddress(req.address());
        return cinemaRepo.save(c);
    }

    public List<Cinema> listCinemas() {
        return cinemaRepo.findAll();
    }

    // ---------- Room (tự sinh ghế) ----------
    @Transactional
    public Room createRoom(RoomRequest req) {
        if (!cinemaRepo.existsById(req.cinemaId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rạp không tồn tại");
        }
        Room room = new Room();
        room.setCinemaId(req.cinemaId());
        room.setName(req.name().trim());
        room.setTotalRows(req.rows());
        room.setSeatsPerRow(req.seatsPerRow());
        room = roomRepo.save(room);

        List<Seat> seats = new ArrayList<>();
        for (int r = 0; r < req.rows(); r++) {
            // 2 hàng cuối là ghế VIP (nếu phòng từ 4 hàng trở lên)
            boolean vip = req.rows() >= 4 && r >= req.rows() - 2;
            for (int n = 1; n <= req.seatsPerRow(); n++) {
                Seat s = new Seat();
                s.setRoomId(room.getId());
                s.setRowLabel(String.valueOf((char) ('A' + r)));
                s.setSeatNumber(n);
                s.setSeatType(vip ? SeatType.VIP : SeatType.NORMAL);
                seats.add(s);
            }
        }
        seatRepo.saveAll(seats);
        return room;
    }

    public List<Room> listRooms(Long cinemaId) {
        return cinemaId == null ? roomRepo.findAll() : roomRepo.findByCinemaId(cinemaId);
    }

    // ---------- Showtime ----------
    @Transactional
    public ShowtimeResponse createShowtime(ShowtimeRequest req) {
        Room room = roomRepo.findById(req.roomId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phòng không tồn tại"));

        // Gọi Movie Service: validate phim + lấy snapshot tên phim và thời lượng
        MovieInfo movie = movieClient.getMovie(req.movieId());

        LocalDateTime end = req.startTime().plusMinutes(movie.durationMinutes());
        if (showtimeRepo.countOverlap(room.getId(), req.startTime(), end) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phòng đã có suất chiếu trùng giờ");
        }

        Showtime st = new Showtime();
        st.setMovieId(movie.id());
        st.setMovieTitle(movie.title());
        st.setRoomId(room.getId());
        st.setStartTime(req.startTime());
        st.setEndTime(end);
        st.setBasePrice(req.basePrice());
        st = showtimeRepo.save(st);

        // Sinh dòng trạng thái cho từng ghế của phòng
        List<ShowtimeSeat> rows = new ArrayList<>();
        for (Seat seat : seatRepo.findByRoomIdOrderByRowLabelAscSeatNumberAsc(room.getId())) {
            ShowtimeSeat ss = new ShowtimeSeat();
            ss.setShowtimeId(st.getId());
            ss.setSeatId(seat.getId());
            rows.add(ss);
        }
        showtimeSeatRepo.saveAll(rows);
        return toResponse(st);
    }

    public List<ShowtimeResponse> listShowtimes(Long movieId) {
        LocalDateTime now = LocalDateTime.now();
        List<Showtime> list = (movieId == null)
                ? showtimeRepo.findByStartTimeAfterOrderByStartTime(now)
                : showtimeRepo.findByMovieIdAndStartTimeAfterOrderByStartTime(movieId, now);
        return list.stream().map(this::toResponse).toList();
    }

    public ShowtimeResponse getShowtime(Long id) {
        return toResponse(findShowtime(id));
    }

    // ---------- Sơ đồ ghế ----------
    public List<SeatView> getSeats(Long showtimeId) {
        Showtime st = findShowtime(showtimeId);
        LocalDateTime now = LocalDateTime.now();

        Map<Long, ShowtimeSeat> states = showtimeSeatRepo.findByShowtimeId(showtimeId).stream()
                .collect(Collectors.toMap(ShowtimeSeat::getSeatId, Function.identity()));

        return seatRepo.findByRoomIdOrderByRowLabelAscSeatNumberAsc(st.getRoomId()).stream().map(seat -> {
            ShowtimeSeat ss = states.get(seat.getId());
            SeatStatus status = ss == null ? SeatStatus.AVAILABLE : ss.getStatus();
            // Ghế HOLDING đã quá hạn được coi như trống
            if (status == SeatStatus.HOLDING && ss.getHoldExpiresAt() != null
                    && ss.getHoldExpiresAt().isBefore(now)) {
                status = SeatStatus.AVAILABLE;
            }
            return new SeatView(seat.getId(), seat.label(), seat.getRowLabel(), seat.getSeatNumber(),
                    seat.getSeatType().name(), priceOf(st, seat), status.name());
        }).toList();
    }

    // ---------- helper dùng chung ----------
    public static long priceOf(Showtime st, Seat seat) {
        return seat.getSeatType() == SeatType.VIP ? st.getBasePrice() * 3 / 2 : st.getBasePrice();
    }

    Showtime findShowtime(Long id) {
        return showtimeRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy suất chiếu"));
    }

    private ShowtimeResponse toResponse(Showtime st) {
        Room room = roomRepo.findById(st.getRoomId()).orElse(null);
        String roomName = room == null ? "" : room.getName();
        String cinemaName = room == null ? "" : cinemaRepo.findById(room.getCinemaId())
                .map(Cinema::getName).orElse("");
        return new ShowtimeResponse(st.getId(), st.getMovieId(), st.getMovieTitle(), st.getRoomId(),
                roomName, cinemaName, st.getStartTime(), st.getEndTime(), st.getBasePrice());
    }
}