import { useCallback, useEffect, useState } from 'react';
import api, { errMsg, fmt, money, imgUrl } from '../api';

function useMsg() {
    const [m, setM] = useState({ text: '', ok: true });
    return [m, (text, ok = true) => setM({ text, ok })];
}

function Msg({ m }) {
    return m.text ? <p className={m.ok ? 'ok' : 'msg'}>{m.text}</p> : null;
}

/* ---------------- Tab Phim ---------------- */
function MoviesTab() {
    const empty = { title: '', description: '', durationMinutes: 100, genre: '', posterUrl: '', releaseDate: '' };
    const [f, setF] = useState(empty);
    const [list, setList] = useState([]);
    const [m, setMsg] = useMsg();
    const [uploading, setUploading] = useState(false);
    const set = (k) => (e) => setF({ ...f, [k]: e.target.value });

    const load = useCallback(() => api.get('/api/movies').then((r) => setList(r.data)), []);
    useEffect(() => { load(); }, [load]);

    const upload = async (file) => {
        const fd = new FormData();
        fd.append('file', file);
        const { data } = await api.post('/api/movies/upload', fd);
        return data.url;
    };

    // Chọn ảnh cho form thêm phim
    const pickForForm = async (e) => {
        const file = e.target.files[0];
        if (!file) return;
        setUploading(true);
        try { const url = await upload(file); setF((x) => ({ ...x, posterUrl: url })); setMsg('Đã tải ảnh lên.'); }
        catch (err) { setMsg(errMsg(err), false); }
        finally { setUploading(false); }
    };

    // Đổi ảnh cho phim đã có
    const changePoster = async (movie, file) => {
        if (!file) return;
        try {
            const url = await upload(file);
            await api.put(`/api/movies/${movie.id}`, {
                title: movie.title, description: movie.description, durationMinutes: movie.durationMinutes,
                genre: movie.genre, posterUrl: url, releaseDate: movie.releaseDate,
            });
            setMsg(`Đã đổi ảnh phim "${movie.title}".`);
            load();
        } catch (err) { setMsg(errMsg(err), false); }
    };

    const submit = async (e) => {
        e.preventDefault();
        try {
            await api.post('/api/movies', { ...f, durationMinutes: Number(f.durationMinutes), releaseDate: f.releaseDate || null });
            setMsg('Đã tạo phim.');
            setF(empty);
            load();
        } catch (err) { setMsg(errMsg(err), false); }
    };

    const del = async (id) => {
        if (!confirm('Ngừng chiếu phim này?')) return;
        try { await api.delete(`/api/movies/${id}`); setMsg('Đã ngừng chiếu.'); load(); }
        catch (err) { setMsg(errMsg(err), false); }
    };

    return (
        <>
            <form className="card form wide" onSubmit={submit}>
                <h3>Thêm phim</h3>
                <input placeholder="Tên phim" value={f.title} onChange={set('title')} required />
                <textarea placeholder="Mô tả" value={f.description} onChange={set('description')} rows={3} />
                <div className="row">
                    <input type="number" min="1" max="600" placeholder="Thời lượng (phút)" value={f.durationMinutes} onChange={set('durationMinutes')} required />
                    <input placeholder="Thể loại" value={f.genre} onChange={set('genre')} />
                    <input type="date" value={f.releaseDate} onChange={set('releaseDate')} />
                </div>
                <label>Ảnh poster (chọn từ máy, tối đa 5MB)
                    <input type="file" accept="image/*" onChange={pickForForm} disabled={uploading} />
                </label>
                {f.posterUrl && <img src={imgUrl(f.posterUrl)} alt="preview" style={{ height: 140, borderRadius: 8, alignSelf: 'flex-start' }} />}
                <Msg m={m} />
                <button className="btn" disabled={uploading}>{uploading ? 'Đang tải ảnh...' : 'Tạo phim'}</button>
            </form>

            <h3>Danh sách phim</h3>
            <table className="table">
                <thead><tr><th>Ảnh</th><th>ID</th><th>Tên</th><th>Khởi chiếu</th><th>Đổi ảnh</th><th /></tr></thead>
                <tbody>
                {list.map((x) => (
                    <tr key={x.id}>
                        <td>{x.posterUrl ? <img src={imgUrl(x.posterUrl)} alt="" style={{ height: 48, borderRadius: 4 }} /> : '🎬'}</td>
                        <td>{x.id}</td><td>{x.title}</td><td>{x.releaseDate}</td>
                        <td><input type="file" accept="image/*" onChange={(e) => { changePoster(x, e.target.files[0]); e.target.value = ''; }} /></td>
                        <td><button className="btn small ghost" onClick={() => del(x.id)}>Ngừng chiếu</button></td>
                    </tr>
                ))}
                </tbody>
            </table>
        </>
    );
}
/* ---------------- Tab Rạp & phòng ---------------- */
function CinemasTab() {
    const [cinemas, setCinemas] = useState([]);
    const [rooms, setRooms] = useState([]);
    const [c, setC] = useState({ name: '', address: '' });
    const [r, setR] = useState({ cinemaId: '', name: '', rows: 5, seatsPerRow: 8 });
    const [mc, setMc] = useMsg();
    const [mr, setMr] = useMsg();

    const load = useCallback(async () => {
        const [a, b] = await Promise.all([api.get('/api/cinemas'), api.get('/api/rooms')]);
        setCinemas(a.data);
        setRooms(b.data);
    }, []);
    useEffect(() => { load(); }, [load]);

    const addCinema = async (e) => {
        e.preventDefault();
        try { await api.post('/api/cinemas', c); setMc('Đã tạo rạp.'); setC({ name: '', address: '' }); load(); }
        catch (err) { setMc(errMsg(err), false); }
    };

    const addRoom = async (e) => {
        e.preventDefault();
        try {
            await api.post('/api/rooms', {
                cinemaId: Number(r.cinemaId), name: r.name, rows: Number(r.rows), seatsPerRow: Number(r.seatsPerRow),
            });
            setMr('Đã tạo phòng và tự sinh ghế.');
            setR({ ...r, name: '' });
            load();
        } catch (err) { setMr(errMsg(err), false); }
    };

    return (
        <>
            <form className="card form wide" onSubmit={addCinema}>
                <h3>Thêm rạp</h3>
                <input placeholder="Tên rạp" value={c.name} onChange={(e) => setC({ ...c, name: e.target.value })} required />
                <input placeholder="Địa chỉ" value={c.address} onChange={(e) => setC({ ...c, address: e.target.value })} />
                <Msg m={mc} />
                <button className="btn">Tạo rạp</button>
            </form>

            <form className="card form wide" onSubmit={addRoom}>
                <h3>Thêm phòng chiếu</h3>
                <select value={r.cinemaId} onChange={(e) => setR({ ...r, cinemaId: e.target.value })} required>
                    <option value="">-- Chọn rạp --</option>
                    {cinemas.map((x) => <option key={x.id} value={x.id}>{x.name}</option>)}
                </select>
                <input placeholder="Tên phòng" value={r.name} onChange={(e) => setR({ ...r, name: e.target.value })} required />
                <div className="row">
                    <label>Số hàng (tối đa 26) <input type="number" min="1" max="26" value={r.rows} onChange={(e) => setR({ ...r, rows: e.target.value })} /></label>
                    <label>Ghế mỗi hàng (tối đa 30) <input type="number" min="1" max="30" value={r.seatsPerRow} onChange={(e) => setR({ ...r, seatsPerRow: e.target.value })} /></label>
                </div>
                <p className="muted">Hai hàng cuối tự động là ghế VIP (phòng từ 4 hàng trở lên).</p>
                <Msg m={mr} />
                <button className="btn">Tạo phòng</button>
            </form>

            <h3>Rạp và phòng hiện có</h3>
            {cinemas.map((x) => (
                <div key={x.id} className="card">
                    <b>{x.name}</b> <span className="muted">(id {x.id}) · {x.address}</span>
                    <ul>
                        {rooms.filter((y) => y.cinemaId === x.id).map((y) => (
                            <li key={y.id}>{y.name} (id {y.id}): {y.totalRows} hàng × {y.seatsPerRow} ghế</li>
                        ))}
                    </ul>
                </div>
            ))}
        </>
    );
}

/* ---------------- Tab Suất chiếu ---------------- */
function ShowtimesTab() {
    const [movies, setMovies] = useState([]);
    const [cinemas, setCinemas] = useState([]);
    const [rooms, setRooms] = useState([]);
    const [list, setList] = useState([]);
    const [f, setF] = useState({ movieId: '', cinemaId: '', roomId: '', startTime: '', basePrice: 90000 });
    const [m, setMsg] = useMsg();

    const loadList = useCallback(() => api.get('/api/showtimes').then((r) => setList(r.data)), []);

    useEffect(() => {
        api.get('/api/movies').then((r) => setMovies(r.data));
        api.get('/api/cinemas').then((r) => setCinemas(r.data));
        api.get('/api/rooms').then((r) => setRooms(r.data));
        loadList();
    }, [loadList]);

    const roomsOfCinema = rooms.filter((x) => String(x.cinemaId) === String(f.cinemaId));

    const submit = async (e) => {
        e.preventDefault();
        try {
            const start = f.startTime.length === 16 ? f.startTime + ':00' : f.startTime;
            await api.post('/api/showtimes', {
                movieId: Number(f.movieId), roomId: Number(f.roomId), startTime: start, basePrice: Number(f.basePrice),
            });
            setMsg('Đã tạo suất chiếu.');
            loadList();
        } catch (err) {
            setMsg(
                err.response?.status === 409 ? 'Phòng đã có suất chiếu trùng giờ.' : errMsg(err),
                false
            );
        }
    };

    return (
        <>
            <form className="card form wide" onSubmit={submit}>
                <h3>Thêm suất chiếu</h3>
                <select value={f.movieId} onChange={(e) => setF({ ...f, movieId: e.target.value })} required>
                    <option value="">-- Chọn phim --</option>
                    {movies.map((x) => <option key={x.id} value={x.id}>{x.title} ({x.durationMinutes} phút)</option>)}
                </select>
                <select value={f.cinemaId} onChange={(e) => setF({ ...f, cinemaId: e.target.value, roomId: '' })} required>
                    <option value="">-- Chọn rạp --</option>
                    {cinemas.map((x) => <option key={x.id} value={x.id}>{x.name}</option>)}
                </select>
                <select value={f.roomId} onChange={(e) => setF({ ...f, roomId: e.target.value })} required disabled={!f.cinemaId}>
                    <option value="">-- Chọn phòng --</option>
                    {roomsOfCinema.map((x) => <option key={x.id} value={x.id}>{x.name}</option>)}
                </select>
                <div className="row">
                    <label>Giờ chiếu <input type="datetime-local" value={f.startTime} onChange={(e) => setF({ ...f, startTime: e.target.value })} required /></label>
                    <label>Giá vé thường (VND) <input type="number" min="1000" step="1000" value={f.basePrice} onChange={(e) => setF({ ...f, basePrice: e.target.value })} required /></label>
                </div>
                <p className="muted">Ghế VIP tự tính giá ×1.5. Giờ kết thúc tính theo thời lượng phim.</p>
                <Msg m={m} />
                <button className="btn">Tạo suất chiếu</button>
            </form>

            <h3>Suất chiếu sắp tới</h3>
            <table className="table">
                <thead><tr><th>ID</th><th>Phim</th><th>Rạp / Phòng</th><th>Bắt đầu</th><th>Kết thúc</th><th>Giá</th></tr></thead>
                <tbody>
                {list.map((x) => (
                    <tr key={x.id}>
                        <td>{x.id}</td><td>{x.movieTitle}</td><td>{x.cinemaName} · {x.roomName}</td>
                        <td>{fmt(x.startTime)}</td><td>{fmt(x.endTime)}</td><td>{money(x.basePrice)}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </>
    );
}

/* ---------------- Trang Admin ---------------- */
export default function Admin() {
    const [tab, setTab] = useState('showtimes');
    const tabs = [['movies', 'Phim'], ['cinemas', 'Rạp & phòng'], ['showtimes', 'Suất chiếu']];
    return (
        <>
            <h2>Quản trị</h2>
            <div className="tabs">
                {tabs.map(([k, label]) => (
                    <button key={k} className={`tab ${tab === k ? 'active' : ''}`} onClick={() => setTab(k)}>{label}</button>
                ))}
            </div>
            {tab === 'movies' && <MoviesTab />}
            {tab === 'cinemas' && <CinemasTab />}
            {tab === 'showtimes' && <ShowtimesTab />}
        </>
    );
}