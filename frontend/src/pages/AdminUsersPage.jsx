import { useState } from "react";
import { useOutletContext } from "react-router-dom";
import { useAsync } from "../hooks/useAsync";
import { deleteUser, getUsers } from "../services/userService";
import { ROLE_LABEL } from "../data/content";
import { formatDate } from "../utils/format";
import { isAdmin } from "../utils/roles";
import LoadState from "../components/LoadState";
import TeacherFormModal from "../components/TeacherFormModal";
import UserDetailModal from "../components/UserDetailModal";

const ROLE_FILTERS = [
  { value: "", label: "Tất cả" },
  { value: "ADMIN", label: "Quản trị viên" },
  { value: "TEACHER", label: "Giáo viên" },
  { value: "STUDENT", label: "Học viên" },
];

export default function AdminUsersPage() {
  const { auth, openAuth } = useOutletContext();
  const admin = isAdmin(auth.user);
  const users = useAsync(() => (admin ? getUsers() : Promise.resolve([])), [auth.user?.id, admin]);

  const [role, setRole] = useState("");
  const [query, setQuery] = useState("");
  const [detailId, setDetailId] = useState(null);
  const [addingTeacher, setAddingTeacher] = useState(false);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");

  if (auth.loading) return <div className="page"><div className="wrap"><p className="state">Đang tải...</p></div></div>;
  if (!auth.user) {
    return (
      <div className="page"><div className="wrap">
        <div className="state">
          <p>Vui lòng đăng nhập để tiếp tục.</p>
          <button className="btn" onClick={() => openAuth("login")}>Đăng nhập</button>
        </div>
      </div></div>
    );
  }
  if (!admin) {
    return <div className="page"><div className="wrap"><p className="state state--error">Chỉ quản trị viên mới xem được trang này.</p></div></div>;
  }

  const all = users.data || [];
  const q = query.trim().toLowerCase();
  const list = all.filter((u) => {
    if (role && u.role !== role) return false;
    if (!q) return true;
    return [u.username, u.fullName, u.email, u.phone].some((v) => (v || "").toLowerCase().includes(q));
  });
  const countOf = (value) => (value ? all.filter((u) => u.role === value).length : all.length);

  const remove = async (u) => {
    const ok = window.confirm(
      `Xóa tài khoản "${u.username}"?\nDữ liệu đăng ký học và tiến độ học của tài khoản này cũng bị xóa. ` +
        "Nếu là giáo viên đang có khóa học, hệ thống sẽ không cho xóa."
    );
    if (!ok) return;
    setError("");
    setNotice("");
    try {
      await deleteUser(u.id);
      setNotice(`Đã xóa tài khoản ${u.username}.`);
      users.reload();
    } catch (e) {
      setError(e.message);
    }
  };

  return (
    <div className="page">
      <div className="wrap">
        <div className="section-head">
          <h1 className="page-title">Quản lý người dùng</h1>
          <button className="btn" onClick={() => setAddingTeacher(true)}>+ Thêm giáo viên</button>
        </div>
        <p className="page-sub">Xem, tìm kiếm và xóa tài khoản. Tài khoản giáo viên do quản trị viên tạo tại đây.</p>

        <div className="chips">
          {ROLE_FILTERS.map((f) => (
            <button key={f.value} className={role === f.value ? "on" : ""} onClick={() => setRole(f.value)}>
              {f.label} ({countOf(f.value)})
            </button>
          ))}
        </div>

        <LoadState loading={users.loading} error={users.error} openAuth={openAuth} />
        {error && <p className="state state--error" role="alert">{error}</p>}
        {notice && <p className="notice" role="status">{notice}</p>}
        {!users.loading && !users.error && list.length === 0 && <p className="state">Không có người dùng phù hợp.</p>}

        {list.length > 0 && (
          <div className="table-wrap">
            <table className="members">
              <thead>
                <tr>
                  <th>Tên đăng nhập</th><th>Họ và tên</th><th>Email</th><th>Số điện thoại</th><th>Vai trò</th><th>Ngày tạo</th><th></th>
                </tr>
              </thead>
              <tbody>
                {list.map((u) => {
                  const isSelf = u.id === auth.user.id;
                  return (
                    <tr key={u.id}>
                      <td>{u.username}{isSelf && <span className="badge badge--info">Bạn</span>}</td>
                      <td>{u.fullName || "—"}</td>
                      <td>{u.email}</td>
                      <td>{u.phone || "—"}</td>
                      <td><span className={`role-badge role-badge--${u.role}`}>{ROLE_LABEL[u.role] || u.role}</span></td>
                      <td>{formatDate(u.createdAt) || "—"}</td>
                      <td>
                        <div className="lrow-actions">
                          <button className="mini" onClick={() => setDetailId(u.id)}>Xem</button>
                          <button className="mini mini--danger" disabled={isSelf} title={isSelf ? "Không thể xóa chính mình" : ""} onClick={() => remove(u)}>Xóa</button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {detailId && <UserDetailModal userId={detailId} onClose={() => setDetailId(null)} />}
      {addingTeacher && (
        <TeacherFormModal
          onClose={() => setAddingTeacher(false)}
          onSaved={(t) => {
            setAddingTeacher(false);
            setError("");
            setNotice(`Đã tạo giáo viên ${t?.username ?? ""}.`.trim());
            users.reload();
          }}
        />
      )}
    </div>
  );
}
