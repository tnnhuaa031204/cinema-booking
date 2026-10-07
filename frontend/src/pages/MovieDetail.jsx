import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import api, { errMsg, fmt, money } from '../api';

export default function MovieDetail() {
    const { id } = useParams();
    const [movie, setMovie] = useState(null);
    const [showtimes, setShowtimes] = useState([]);
    const [err, setErr] = useState('');

    useEffect(() => {
        api.get(`/api/movies/${id}`).then((r) => setMovie(r.data)).catch((e) => setErr(errMsg(e)));
        api.get('/api/showtimes', { params: { movieId: id } }).then((r) => setShowtimes(r.data));
    }, [id]);

    if (err) return <p className="msg">{err}</p>;
    if (!movie) return <p>Đang tải...</p>;

    return (
        <>
            <div className="card">
                <h2>{movie.title}</h2>
                <p className="muted">{movie.genre} · {movie.durationMinutes} phút · Khởi chiếu {movie.releaseDate}</p>
                <p>{movie.description}</p>
            </div>
            <h3>Suất chiếu</h3>
            {showtimes.length === 0 && <p className="muted">Chưa có suất chiếu sắp tới.</p>}
            {showtimes.map((s) => (
                <div key={s.id} className="card row between">
                    <div>
                        <b>{fmt(s.startTime)}</b>
                        <div className="muted">{s.cinemaName} · {s.roomName} · từ {money(s.basePrice)}</div>
                    </div>
                    <Link className="btn" to={`/showtimes/${s.id}`}>Chọn ghế</Link>
                </div>
            ))}
        </>
    );
}