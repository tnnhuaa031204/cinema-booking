import axios from 'axios';

const api = axios.create({ baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080' });

api.interceptors.request.use((c) => {
    const t = localStorage.getItem('token');
    if (t) c.headers.Authorization = `Bearer ${t}`;
    return c;
});

api.interceptors.response.use(
    (r) => r,
    (e) => {
        const url = e.config?.url || '';
        if (e.response?.status === 401 && !url.includes('/api/auth/')) {
            localStorage.clear();
            window.location.href = '/login';
        }
        return Promise.reject(e);
    }
);

export const errMsg = (e) => e.response?.data?.message || e.response?.data?.error || e.message;
export const money = (n) => Number(n).toLocaleString('vi-VN') + 'đ';
export const fmt = (s) => (s ? new Date(s).toLocaleString('vi-VN') : '');
export const getUser = () => JSON.parse(localStorage.getItem('user') || 'null');

export default api;
// Chuyển posterUrl thành URL hiển thị được: ảnh upload đi qua Gateway, ảnh /posters/ và http giữ nguyên
export const imgUrl = (u) => {
    if (!u) return '';
    if (u.startsWith('/api/')) return (import.meta.env.VITE_API_URL || 'http://localhost:8080') + u;
    return u;
};