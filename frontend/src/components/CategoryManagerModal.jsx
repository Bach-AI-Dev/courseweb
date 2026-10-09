import { useEffect, useState } from "react";
import { deleteCategory } from "../services/courseService";
import CategoryFormModal from "./CategoryFormModal";

// Quản lý danh mục: thêm, sửa (ADMIN, TEACHER) và xóa (chỉ ADMIN).
// Danh mục đang có khóa học thì backend không cho xóa.
export default function CategoryManagerModal({ categories, isAdmin, onClose, onChanged }) {
  const [form, setForm] = useState(null); // null | { category: danh mục cũ hoặc null }
  const [error, setError] = useState("");

  useEffect(() => {
    const onKey = (e) => e.key === "Escape" && !form && onClose();
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [onClose, form]);

  const remove = async (c) => {
    if (!window.confirm(`Xóa danh mục "${c.name}"?`)) return;
    setError("");
    try {
      await deleteCategory(c.id);
      onChanged();
    } catch (e) {
      setError(e.message);
    }
  };

  return (
    <>
      <div className="overlay" onClick={onClose}>
        <div className="modal modal--form" role="dialog" aria-modal="true" onClick={(e) => e.stopPropagation()}>
          <button className="modal__close" aria-label="Đóng" onClick={onClose}>×</button>
          <h3>Quản lý danh mục</h3>

          {categories.length === 0 && <p className="state">Chưa có danh mục nào.</p>}
          <ul className="cat-list">
            {categories.map((c) => (
              <li key={c.id}>
                <div>
                  <b>{c.name}</b>
                  {c.description && <small>{c.description}</small>}
                </div>
                <div className="lrow-actions">
                  <button className="mini" onClick={() => setForm({ category: c })}>Sửa</button>
                  {isAdmin && <button className="mini mini--danger" onClick={() => remove(c)}>Xóa</button>}
                </div>
              </li>
            ))}
          </ul>

          {error && <p className="err" role="alert">{error}</p>}
          <button className="btn" onClick={() => setForm({ category: null })}>+ Thêm danh mục</button>
        </div>
      </div>

      {form && (
        <CategoryFormModal
          category={form.category}
          onClose={() => setForm(null)}
          onSaved={() => { setForm(null); onChanged(); }}
        />
      )}
    </>
  );
}
