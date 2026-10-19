import { useEffect, useState } from "react";
import { createLesson, updateLesson } from "../services/courseService";
import { isHttpLink } from "../utils/format";

// Thêm bài học (lesson = null) hoặc sửa bài học (lesson = dữ liệu cũ).
// Mỗi bài học đều có video (bắt buộc) và link bài tập (tùy chọn).
export default function LessonFormModal({ courseId, lesson, nextOrder = 1, onClose, onSaved }) {
  const editing = Boolean(lesson);
  const [form, setForm] = useState({
    name: lesson?.name ?? "",
    lessonOrder: lesson?.lessonOrder ?? nextOrder,
    videoUrl: lesson?.videoUrl ?? "",
    duration: lesson?.duration ?? "",
    assignmentUrl: lesson?.assignmentUrl ?? "",
  });
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
    const assignmentUrl = form.assignmentUrl.trim();
    if (!form.name.trim()) return setError("Vui lòng nhập tên bài học.");
    if (!(Number(form.lessonOrder) >= 1)) return setError("Thứ tự phải từ 1 trở lên.");
    if (!form.videoUrl.trim()) return setError("Vui lòng nhập đường dẫn video.");
    if (!(Number(form.duration) >= 1)) return setError("Thời lượng video phải lớn hơn 0 (tính bằng giây).");
    if (assignmentUrl && !isHttpLink(assignmentUrl)) return setError("Link bài tập phải bắt đầu bằng http:// hoặc https://");

    setBusy(true);
    setError("");
    try {
      const body = {
        name: form.name.trim(),
        lessonOrder: Number(form.lessonOrder),
        videoUrl: form.videoUrl.trim(),
        duration: Number(form.duration),
        assignmentUrl: assignmentUrl || null,
      };
      const saved = editing ? await updateLesson(lesson.id, body) : await createLesson(courseId, body);
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
        <h3>{editing ? "Sửa bài học" : "Thêm bài học"}</h3>

        <form onSubmit={submit}>
          <label className="field"><span>Tên bài học *</span>
            <input value={form.name} onChange={set("name")} />
          </label>
          <label className="field"><span>Thứ tự *</span>
            <input type="number" min="1" value={form.lessonOrder} onChange={set("lessonOrder")} />
          </label>

          <label className="field"><span>Đường dẫn video *</span>
            <input placeholder="https://.../bai-1.mp4 hoặc link YouTube" value={form.videoUrl} onChange={set("videoUrl")} />
            <small>Link YouTube phát được nhưng không theo dõi được tiến độ học.</small>
          </label>
          <label className="field"><span>Thời lượng video (giây) *</span>
            <input type="number" min="1" value={form.duration} onChange={set("duration")} />
          </label>

          <label className="field"><span>Link bài tập (không bắt buộc)</span>
            <input placeholder="https://forms.gle/... hoặc link Google Drive" value={form.assignmentUrl} onChange={set("assignmentUrl")} />
            <small>Để trống nếu bài học này chưa có bài tập.</small>
          </label>

          {error && <p className="err" role="alert">{error}</p>}
          <button className="btn" type="submit" disabled={busy}>{busy ? "Đang lưu..." : "Lưu"}</button>
        </form>
      </div>
    </div>
  );
}
