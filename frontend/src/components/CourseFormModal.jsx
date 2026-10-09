import { useEffect, useState } from "react";
import { createCourse, updateCourse } from "../services/courseService";
import CategoryFormModal from "./CategoryFormModal";

// CourseStatus của backend: DRAFT, PUBLISHED, ARCHIVED. Chỉ PUBLISHED mới cho học viên đăng ký.
const STATUS_OPTIONS = [
  { value: "DRAFT", label: "Nháp (chưa mở đăng ký)" },
  { value: "PUBLISHED", label: "Công khai (cho đăng ký)" },
  { value: "ARCHIVED", label: "Lưu trữ (ẩn)" },
];

// Thêm khóa học (course = null) hoặc sửa khóa học (course = dữ liệu cũ).
// Backend khi sửa chỉ cập nhật: tên, mô tả, giá, trạng thái, ảnh. Danh mục và giáo viên chỉ chọn lúc tạo.
export default function CourseFormModal({ course, categories = [], teachers = [], currentUser, onClose, onSaved, onCategoryAdded }) {
  const editing = Boolean(course);
  const isAdmin = currentUser.role === "ADMIN";
  const [cats, setCats] = useState(categories);
  const [showCategory, setShowCategory] = useState(false);
  const [form, setForm] = useState({
    title: course?.title ?? "",
    description: course?.description ?? "",
    thumbnailUrl: course?.thumbnailUrl ?? "",
    price: course?.price ?? 0,
    status: course?.status ?? "DRAFT",
    categoryId: course?.categoryId ?? categories[0]?.id ?? "",
    teacherId: course?.teacherId ?? currentUser.id,
  });
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);
  const set = (key) => (e) => setForm({ ...form, [key]: e.target.value });

  useEffect(() => {
    // Đang mở cửa sổ tạo danh mục thì Esc chỉ đóng cửa sổ đó
    const onKey = (e) => e.key === "Escape" && !showCategory && onClose();
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [onClose, showCategory]);

  const submit = async (e) => {
    e.preventDefault();
    if (!form.title.trim()) return setError("Vui lòng nhập tên khóa học.");
    if (Number(form.price) < 0) return setError("Giá không được âm.");
    if (!editing && !form.categoryId) return setError("Chưa chọn danh mục. Hãy bấm \"+ Tạo danh mục mới\".");
    setBusy(true);
    setError("");
    try {
      const body = { ...form, title: form.title.trim(), price: Number(form.price) || 0 };
      const saved = editing ? await updateCourse(course.id, body) : await createCourse(body);
      onSaved(saved);
    } catch (err) {
      setError(err.message);
      setBusy(false);
    }
  };

  return (
    <>
      <div className="overlay" onClick={onClose}>
        <div className="modal modal--form" role="dialog" aria-modal="true" onClick={(e) => e.stopPropagation()}>
          <button className="modal__close" aria-label="Đóng" onClick={onClose}>×</button>
          <h3>{editing ? "Sửa khóa học" : "Thêm khóa học"}</h3>

          <form onSubmit={submit}>
            <label className="field"><span>Tên khóa học *</span>
              <input value={form.title} onChange={set("title")} />
            </label>
            <label className="field"><span>Mô tả</span>
              <textarea rows={3} value={form.description} onChange={set("description")} />
            </label>
            <label className="field"><span>Link ảnh bìa</span>
              <input placeholder="https://..." value={form.thumbnailUrl} onChange={set("thumbnailUrl")} />
            </label>
            <label className="field"><span>Giá (đ, nhập 0 nếu miễn phí)</span>
              <input type="number" min="0" value={form.price} onChange={set("price")} />
            </label>
            <label className="field"><span>Trạng thái</span>
              <select value={form.status} onChange={set("status")}>
                {STATUS_OPTIONS.map((o) => <option key={o.value} value={o.value}>{o.label}</option>)}
              </select>
            </label>

            {!editing && (
              <div className="field">
                <span>Danh mục *</span>
                <select value={form.categoryId} onChange={set("categoryId")}>
                  {cats.length === 0 && <option value="">(chưa có danh mục)</option>}
                  {cats.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
                </select>
                <button type="button" className="link-btn" onClick={() => setShowCategory(true)}>+ Tạo danh mục mới</button>
              </div>
            )}
            {!editing && isAdmin && teachers.length > 0 && (
              <label className="field"><span>Giáo viên phụ trách</span>
                <select value={form.teacherId} onChange={set("teacherId")}>
                  {teachers.map((u) => <option key={u.id} value={u.id}>{u.fullName || u.username} ({u.role})</option>)}
                </select>
              </label>
            )}

            {error && <p className="err" role="alert">{error}</p>}
            <button className="btn" type="submit" disabled={busy}>{busy ? "Đang lưu..." : "Lưu"}</button>
          </form>
        </div>
      </div>

      {showCategory && (
        <CategoryFormModal
          onClose={() => setShowCategory(false)}
          onSaved={(category) => {
            setCats((list) => [...list, category]);
            setForm((f) => ({ ...f, categoryId: category.id })); // chọn luôn danh mục vừa tạo
            setShowCategory(false);
            onCategoryAdded?.();
          }}
        />
      )}
    </>
  );
}
