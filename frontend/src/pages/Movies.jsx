import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api, { errMsg, imgUrl } from '../api';

function MovieCard({ movie, comingSoon }) {
    return (
        <Link to={`/movies/${movie.id}`} className="card movie">
            <div className="poster">
                {movie.posterUrl ? <img src={imgUrl(movie.posterUrl)} alt={movie.title} /> : <span>🎬</span>}
            </div>
            <div style={{ padding: '8px 0 0' }}>
                <h3 style={{ margin: '0 0 6px', fontSize: '16px' }}>{movie.title}</h3>
                <p className="muted" style={{ margin: '0 0 12px', fontSize: '13px' }}>
                    {movie.genre} {movie.durationMinutes ? `• ${movie.durationMinutes} phút` : ''}
                </p>
                {comingSoon ? (
                    <p className="muted" style={{ margin: 0 }}>Khởi chiếu {movie.releaseDate}</p>
                ) : (
                    <span className="btn small">Mua vé ngay</span>
                )}
            </div>
        </Link>
    );
}

export default function Movies() {
    const [movies, setMovies] = useState([]);
    const [loading, setLoading] = useState(true);
    const [err, setErr] = useState('');

    useEffect(() => {
        api.get('/api/movies')
            .then((r) => setMovies(r.data))
            .catch((e) => setErr(errMsg(e)))
            .finally(() => setLoading(false));
    }, []);

    const today = new Date().toISOString().slice(0, 10);
    const nowShowing = movies.filter((m) => !m.releaseDate || m.releaseDate <= today);
    const comingSoon = movies.filter((m) => m.releaseDate && m.releaseDate > today);

    return (
        <div>
            <div className="card" style={{ background: 'linear-gradient(135deg, #171b24, #232d3f)', marginBottom: 24 }}>
                <h1 style={{ margin: '0 0 8px', fontSize: 28 }}>🎬 Cinema</h1>
                <p className="muted" style={{ margin: 0 }}>Chọn bộ phim yêu thích và đặt vé ngay.</p>
            </div>

            {loading && <p className="muted">Đang tải danh sách phim...</p>}
            {err && <p className="msg">{err}</p>}

            {!loading && !err && (
                <>
                    <h2>Phim đang chiếu</h2>
                    {nowShowing.length === 0 && <p className="muted">Chưa có phim đang chiếu.</p>}
                    <div className="grid">
                        {nowShowing.map((m) => <MovieCard key={m.id} movie={m} />)}
                    </div>

                    <h2 style={{ marginTop: 32 }}>Phim sắp chiếu</h2>
                    {comingSoon.length === 0 && <p className="muted">Chưa có phim sắp chiếu.</p>}
                    <div className="grid">
                        {comingSoon.map((m) => <MovieCard key={m.id} movie={m} comingSoon />)}
                    </div>
                </>
            )}
        </div>
    );
}