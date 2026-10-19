import { useEffect, useState } from "react";
import { createCategory, updateCategory } from "../services/courseService";

// Tạo danh mục mới (category = null) hoặc sửa danh mục (category = dữ liệu cũ).
// Gọi onSaved(danhMục) khi thành công.
export default function CategoryFormModal({ category, onClose, onSaved }) {
  const editing = Boolean(category);
  const [form, setForm] = useState({ name: category?.name ?? "", description: category?.description ?? "" });
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
    const name = form.name.trim();
    if (name.length < 2 || name.length > 100) return setError("Tên danh mục phải từ 2 đến 100 ký tự.");
    setBusy(true);
    setError("");
    try {
      const body = { name, description: form.description.trim() };
      onSaved(editing ? await updateCategory(category.id, body) : await createCategory(body));
    } catch (err) {
      setError(err.message);
      setBusy(false);
    }
  };

  return (
    <div className="overlay" onClick={onClose}>
      <div className="modal modal--form" role="dialog" aria-modal="true" onClick={(e) => e.stopPropagation()}>
        <button className="modal__close" aria-label="Đóng" onClick={onClose}>×</button>
        <h3>{editing ? "Sửa danh mục" : "Thêm danh mục"}</h3>

        <form onSubmit={submit}>
          <label className="field"><span>Tên danh mục *</span>
            <input autoFocus value={form.name} onChange={set("name")} />
          </label>
          <label className="field"><span>Mô tả</span>
            <textarea rows={3} value={form.description} onChange={set("description")} />
          </label>

          {error && <p className="err" role="alert">{error}</p>}
          <button className="btn" type="submit" disabled={busy}>{busy ? "Đang lưu..." : "Lưu"}</button>
        </form>
      </div>
    </div>
  );
}
