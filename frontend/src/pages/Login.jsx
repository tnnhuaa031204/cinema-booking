import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api, { errMsg } from '../api';

export default function Login() {
    const nav = useNavigate();
    const [mode, setMode] = useState('login');
    const [f, setF] = useState({ username: '', email: '', password: '' });
    const [msg, setMsg] = useState('');
    const set = (k) => (e) => setF({ ...f, [k]: e.target.value });

    const submit = async (e) => {
        e.preventDefault();
        setMsg('');
        try {
            if (mode === 'register') {
                await api.post('/api/auth/register', f);
                setMode('login');
                setMsg('Đăng ký thành công, hãy đăng nhập.');
                return;
            }
            const { data } = await api.post('/api/auth/login', { username: f.username, password: f.password });
            localStorage.setItem('token', data.token);
            localStorage.setItem('user', JSON.stringify(data));
            nav('/');
        } catch (err) {
            setMsg(errMsg(err));
        }
    };

    return (
        <form className="card form" onSubmit={submit}>
            <h2>{mode === 'login' ? 'Đăng nhập' : 'Đăng ký'}</h2>
            <input placeholder="Tên đăng nhập" value={f.username} onChange={set('username')} required />
            {mode === 'register' && <input type="email" placeholder="Email" value={f.email} onChange={set('email')} required />}
            <input type="password" placeholder="Mật khẩu (từ 6 ký tự)" value={f.password} onChange={set('password')} required />
            {msg && <p className="msg">{msg}</p>}
            <button className="btn">{mode === 'login' ? 'Đăng nhập' : 'Đăng ký'}</button>
            <a className="link" onClick={() => { setMode(mode === 'login' ? 'register' : 'login'); setMsg(''); }}>
                {mode === 'login' ? 'Chưa có tài khoản? Đăng ký' : 'Đã có tài khoản? Đăng nhập'}
            </a>
        </form>
    );
}