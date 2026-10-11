import { useState } from "react";
import { useOutletContext } from "react-router-dom";
import { ROLE_LABEL } from "../data/content";
import { updateMyInfo } from "../services/userService";
import { formatDate } from "../utils/format";

export default function ProfilePage() {
  const { auth, openAuth } = useOutletContext();

  if (auth.loading) return <div className="page"><div className="wrap"><p className="state">Đang tải...</p></div></div>;
  if (!auth.user) {
    return (
      <div className="page"><div className="wrap">
        <div className="state">
          <p>Vui lòng đăng nhập để xem tài khoản.</p>
          <button className="btn" onClick={() => openAuth("login")}>Đăng nhập</button>
        </div>
      </div></div>
    );
  }
  return <ProfileView key={auth.user.id} user={auth.user} onSaved={auth.setUser} />;
}

function ProfileView({ user, onSaved }) {
  const [form, setForm] = useState({
    fullName: user.fullName ?? "",
    email: user.email ?? "",
    phone: user.phone ?? "",
    password: "",
    confirm: "",
  });
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");
  const [busy, setBusy] = useState(false);
  const set = (key) => (e) => setForm({ ...form, [key]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    setNotice("");
    const email = form.email.trim();
    const fullName = form.fullName.trim();
    const phone = form.phone.trim();

    // Giới hạn theo cột trong database: email 50, họ tên 100, số điện thoại 10
    if (!email) return setError("Vui lòng nhập email.");
    if (!/^\S+@\S+\.\S+$/.test(email)) return setError("Email chưa hợp lệ.");
    if (email.length > 50) return setError("Email tối đa 50 ký tự.");
    if (fullName.length > 100) return setError("Họ và tên tối đa 100 ký tự.");
    if (phone && !/^\d{1,10}$/.test(phone)) return setError("Số điện thoại chỉ gồm chữ số, tối đa 10 số.");
    if (form.password) {
      if (form.password.length < 6) return setError("Mật khẩu mới cần ít nhất 6 ký tự.");
      if (form.password !== form.confirm) return setError("Mật khẩu nhập lại không khớp.");
    }

    setBusy(true);
    setError("");
    try {
      // Luôn gửi đủ email, họ tên, số điện thoại (backend ghi đè bằng giá trị gửi lên)
      const body = { email, fullName: fullName || null, phone: phone || null };
      if (form.password) body.password = form.password;
      const updated = await updateMyInfo(body);
      onSaved(updated);
      setForm((f) => ({ ...f, password: "", confirm: "" }));
      setNotice(form.password ? "Đã lưu thay đổi và đổi mật khẩu." : "Đã lưu thay đổi.");
    } catch (err) {
      setError(err.message);
    }
    setBusy(false);
  };

  return (
    <div className="page">
      <div className="wrap">
        <h1 className="page-title">Tài khoản của tôi</h1>
        <p className="page-sub">Xem và cập nhật thông tin cá nhân.</p>

        <div className="profile-grid">
          <section className="panel">
            <h2>Thông tin tài khoản</h2>
            <dl className="info-list">
              <dt>Tên đăng nhập</dt><dd>{user.username}</dd>
              <dt>Vai trò</dt><dd><span className={`role-badge role-badge--${user.role}`}>{ROLE_LABEL[user.role] || user.role}</span></dd>
              <dt>Ngày tạo</dt><dd>{formatDate(user.createdAt) || "—"}</dd>
              <dt>Cập nhật lần cuối</dt><dd>{formatDate(user.updatedAt) || "—"}</dd>
            </dl>
          </section>

          <section className="panel">
            <h2>Cập nhật thông tin</h2>
            <form onSubmit={submit}>
              <label className="field"><span>Họ và tên</span>
                <input value={form.fullName} onChange={set("fullName")} />
              </label>
              <label className="field"><span>Email *</span>
                <input type="email" value={form.email} onChange={set("email")} />
              </label>
              <label className="field"><span>Số điện thoại</span>
                <input type="tel" maxLength={10} value={form.phone} onChange={set("phone")} />
              </label>

              <label className="field"><span>Mật khẩu mới</span>
                <input type="password" autoComplete="new-password" placeholder="Để trống nếu không đổi" value={form.password} onChange={set("password")} />
              </label>
              {form.password && (
                <label className="field"><span>Nhập lại mật khẩu mới</span>
                  <input type="password" autoComplete="new-password" value={form.confirm} onChange={set("confirm")} />
                </label>
              )}

              {error && <p className="err" role="alert">{error}</p>}
              {notice && <p className="notice" role="status">{notice}</p>}
              <button className="btn" type="submit" disabled={busy}>{busy ? "Đang lưu..." : "Lưu thay đổi"}</button>
            </form>
          </section>
        </div>
      </div>
    </div>
  );
}
