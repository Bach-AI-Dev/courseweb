import { useEffect, useState } from "react";
import { ROLES } from "../data/content";
import "./AuthModal.css";

const EMPTY = { role: ROLES[0].value, fullName: "", username: "", email: "", phone: "", password: "", confirm: "" };

export default function AuthModal({ mode, onModeChange, onClose, auth }) {
  const [form, setForm] = useState(EMPTY);
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);
  const isLogin = mode === "login";
  const set = (key) => (e) => setForm({ ...form, [key]: e.target.value });

  useEffect(() => {
    const onKey = (e) => e.key === "Escape" && onClose();
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [onClose]);

  const submit = async (e) => {
    e.preventDefault();
    setBusy(true);
    const err = await (isLogin ? auth.login(form) : auth.register(form));
    setBusy(false);
    if (err) setError(err);
    else onClose();
  };

  return (
    <div className="overlay" onClick={onClose}>
      <div className="modal" role="dialog" aria-modal="true" onClick={(e) => e.stopPropagation()}>
        <button className="modal__close" aria-label="Đóng" onClick={onClose}>×</button>
        <h3>{isLogin ? "Đăng nhập" : "Tạo tài khoản"}</h3>

        <div className="tabs">
          <button className={isLogin ? "on" : ""} onClick={() => onModeChange("login")}>Đăng nhập</button>
          <button className={!isLogin ? "on" : ""} onClick={() => onModeChange("register")}>Đăng ký</button>
        </div>

        <form onSubmit={submit}>
          {!isLogin && (
            <>
              <div className="lbl">Bạn là</div>
              <div className="roles">
                {ROLES.map((r) => (
                  <button
                    type="button"
                    key={r.value}
                    disabled={r.disabled}
                    className={"role" + (form.role === r.value ? " on" : "")}
                    onClick={() => setForm({ ...form, role: r.value })}
                  >
                    <b>{r.label}</b>
                    <small>{r.desc}</small>
                  </button>
                ))}
              </div>
              <input placeholder="Họ và tên" value={form.fullName} onChange={set("fullName")} />
            </>
          )}

          <input placeholder="Tên đăng nhập" autoComplete="username" value={form.username} onChange={set("username")} />

          {!isLogin && (
            <>
              <input type="email" placeholder="Email (@gmail.com)" autoComplete="email" value={form.email} onChange={set("email")} />
              <input type="tel" placeholder="Số điện thoại (không bắt buộc)" maxLength={10} value={form.phone} onChange={set("phone")} />
            </>
          )}

          <input
            type="password"
            placeholder="Mật khẩu (tối thiểu 6 ký tự)"
            autoComplete={isLogin ? "current-password" : "new-password"}
            value={form.password}
            onChange={set("password")}
          />
          {!isLogin && (
            <input type="password" placeholder="Nhập lại mật khẩu" autoComplete="new-password" value={form.confirm} onChange={set("confirm")} />
          )}

          {error && <p className="err" role="alert">{error}</p>}
          <button className="btn" type="submit" disabled={busy}>
            {busy ? "Đang xử lý..." : isLogin ? "Đăng nhập" : "Đăng ký"}
          </button>
        </form>
      </div>
    </div>
  );
}
