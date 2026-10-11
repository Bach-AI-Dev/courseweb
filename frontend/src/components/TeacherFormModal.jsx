import { useEffect, useState } from "react";
import { createTeacher } from "../services/userService";

// ADMIN tạo tài khoản giáo viên (POST /api/auth/admin/teacher)
export default function TeacherFormModal({ onClose, onSaved }) {
  const [form, setForm] = useState({ fullName: "", username: "", email: "", phone: "", password: "", confirm: "" });
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);
  const set = (key) => (e) => setForm({ ...form, [key]: e.target.value });

  useEffect(() => {
    const onKey = (e) => e.key === "Escape" && onClose();
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [onClose]);

  const submit = async (e) => {
    e.preventDefault();
    const username = form.username.trim();
    const email = form.email.trim();
    const phone = form.phone.trim();
    if (!form.fullName.trim()) return setError("Vui lòng nhập họ và tên.");
    if (!username) return setError("Vui lòng nhập tên đăng nhập.");
    if (!email) return setError("Vui lòng nhập email.");
    if (phone && !/^\d{1,10}$/.test(phone)) return setError("Số điện thoại chỉ gồm chữ số, tối đa 10 số.");
    if (form.password.length < 6) return setError("Mật khẩu cần ít nhất 6 ký tự.");
    if (form.password !== form.confirm) return setError("Mật khẩu nhập lại không khớp.");

    setBusy(true);
    setError("");
    try {
      const saved = await createTeacher({
        username,
        password: form.password,
        email,
        fullName: form.fullName.trim(),
        phone,
      });
      onSaved(saved);
    } catch (err) {
      setError(err.message);
      setBusy(false);
    }
  };

  return (
    <div className="overlay" onClick={onClose}>
      <div className="modal modal--form" role="dialog" aria-modal="true" onClick={(e) => e.stopPropagation()}>
        <button className="modal__close" aria-label="Đóng" onClick={onClose}>×</button>
        <h3>Thêm giáo viên</h3>

        <form onSubmit={submit}>
          <label className="field"><span>Họ và tên *</span>
            <input autoFocus value={form.fullName} onChange={set("fullName")} />
          </label>
          <label className="field"><span>Tên đăng nhập *</span>
            <input autoComplete="off" value={form.username} onChange={set("username")} />
          </label>
          <label className="field"><span>Email *</span>
            <input type="email" placeholder="ten@gmail.com" value={form.email} onChange={set("email")} />
            <small>Hệ thống hiện chỉ chấp nhận email @gmail.com.</small>
          </label>
          <label className="field"><span>Số điện thoại</span>
            <input type="tel" maxLength={10} value={form.phone} onChange={set("phone")} />
          </label>
          <label className="field"><span>Mật khẩu *</span>
            <input type="password" autoComplete="new-password" value={form.password} onChange={set("password")} />
          </label>
          <label className="field"><span>Nhập lại mật khẩu *</span>
            <input type="password" autoComplete="new-password" value={form.confirm} onChange={set("confirm")} />
          </label>

          {error && <p className="err" role="alert">{error}</p>}
          <button className="btn" type="submit" disabled={busy}>{busy ? "Đang tạo..." : "Tạo tài khoản"}</button>
        </form>
      </div>
    </div>
  );
}
