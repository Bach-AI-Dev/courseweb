import { useEffect } from "react";
import { useAsync } from "../hooks/useAsync";
import { getUser } from "../services/userService";
import { ROLE_LABEL } from "../data/content";
import { formatDate } from "../utils/format";
import LoadState from "./LoadState";

// Chi tiết một người dùng (GET /api/users/admin/{userId}, chỉ ADMIN)
export default function UserDetailModal({ userId, onClose }) {
  const { data: u, loading, error } = useAsync(() => getUser(userId), [userId]);

  useEffect(() => {
    const onKey = (e) => e.key === "Escape" && onClose();
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [onClose]);

  return (
    <div className="overlay" onClick={onClose}>
      <div className="modal modal--form" role="dialog" aria-modal="true" onClick={(e) => e.stopPropagation()}>
        <button className="modal__close" aria-label="Đóng" onClick={onClose}>×</button>
        <h3>Chi tiết người dùng</h3>
        <LoadState loading={loading} error={error} />
        {u && (
          <dl className="info-list">
            <dt>Mã người dùng</dt><dd>{u.id}</dd>
            <dt>Tên đăng nhập</dt><dd>{u.username}</dd>
            <dt>Họ và tên</dt><dd>{u.fullName || "—"}</dd>
            <dt>Email</dt><dd>{u.email}</dd>
            <dt>Số điện thoại</dt><dd>{u.phone || "—"}</dd>
            <dt>Vai trò</dt><dd><span className={`role-badge role-badge--${u.role}`}>{ROLE_LABEL[u.role] || u.role}</span></dd>
            <dt>Ngày tạo</dt><dd>{formatDate(u.createdAt) || "—"}</dd>
            <dt>Cập nhật lần cuối</dt><dd>{formatDate(u.updatedAt) || "—"}</dd>
          </dl>
        )}
      </div>
    </div>
  );
}
