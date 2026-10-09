import { useState } from "react";
import { Link } from "react-router-dom";
import { NAV, ROLE_LABEL } from "../data/content";
import "./Header.css";

export default function Header({ user, onOpenAuth, onLogout }) {
  const [open, setOpen] = useState(false);

  const openAuth = (mode) => {
    setOpen(false);
    onOpenAuth(mode);
  };

  return (
    <header className="header">
      <div className="wrap bar">
        <Link className="logo" to="/" onClick={() => setOpen(false)}>Course<span>Web</span></Link>
        <button className="burger" aria-label="Mở menu" aria-expanded={open} onClick={() => setOpen(!open)}>☰</button>
        <nav className={open ? "nav open" : "nav"}>
          {NAV.map((item) => (
            <Link key={item.href} to={item.href} onClick={() => setOpen(false)}>{item.label}</Link>
          ))}
          {user ? (
            <>
              {user.role === "STUDENT" && (
                <Link to="/khoa-hoc-cua-toi" onClick={() => setOpen(false)}>Khóa học của tôi</Link>
              )}
              <span className="who">Xin chào, <b>{user.fullName || user.username}</b> ({ROLE_LABEL[user.role] || user.role})</span>
              <button className="btn alt" onClick={onLogout}>Đăng xuất</button>
            </>
          ) : (
            <>
              <button className="btn alt" onClick={() => openAuth("login")}>Đăng nhập</button>
              <button className="btn" onClick={() => openAuth("register")}>Đăng ký</button>
            </>
          )}
        </nav>
      </div>
    </header>
  );
}
