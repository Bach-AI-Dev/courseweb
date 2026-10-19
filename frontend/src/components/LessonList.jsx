import { Link } from "react-router-dom";
import { formatDuration } from "../utils/format";

// Danh sách bài học của khóa (đã sắp theo lessonOrder từ backend), kèm dấu ✓ nếu đã hoàn thành.
// Truyền onEdit/onDelete (chỉ ADMIN, TEACHER) để hiện nút Sửa, Xóa cạnh mỗi bài.
export default function LessonList({ courseId, lessons, progressMap = {}, activeId, onEdit, onDelete }) {
  if (!lessons.length) return <p className="state">Khóa học chưa có bài học.</p>;

  return (
    <div className="lessons">
      {lessons.map((l, i) => {
        const p = progressMap[l.id];
        const done = Boolean(p?.isCompleted);
        const active = l.id === activeId;
        const row = (
          <Link
            key={l.id}
            to={`/khoa-hoc/${courseId}/bai-hoc/${l.id}`}
            className={"lrow" + (done ? " done" : "") + (active ? " active" : "")}
          >
            <span className="lrow__icon">{done ? "✓" : "▶"}</span>
            <span>{i + 1}. {l.name}{l.assignmentUrl && <span className="badge badge--info">Bài tập</span>}</span>
            <span className="lrow__meta">
              {formatDuration(l.duration)}
              {!done && p?.completionPercentage > 0 && ` · ${p.completionPercentage}%`}
            </span>
          </Link>
        );
        if (!onEdit && !onDelete) return row;
        return (
          <div className="lrow-wrap" key={l.id}>
            {row}
            <div className="lrow-actions">
              {onEdit && <button className="mini" onClick={() => onEdit(l)}>Sửa</button>}
              {onDelete && <button className="mini mini--danger" onClick={() => onDelete(l)}>Xóa</button>}
            </div>
          </div>
        );
      })}
    </div>
  );
}
