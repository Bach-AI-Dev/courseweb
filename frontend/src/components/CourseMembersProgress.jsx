import { useAsync } from "../hooks/useAsync";
import { getCourseMembersProgress } from "../services/progressService";
import LoadState from "./LoadState";

// Bảng tiến độ của tất cả học viên trong khóa học (chỉ ADMIN).
export default function CourseMembersProgress({ courseId, openAuth }) {
  const members = useAsync(() => getCourseMembersProgress(courseId), [courseId]);
  const list = members.data || [];

  return (
    <>
      <div className="section-head">
        <h2 className="section-title">Tiến độ học viên{!members.loading && !members.error ? ` (${list.length})` : ""}</h2>
        <button className="btn alt" onClick={members.reload}>Làm mới</button>
      </div>
      <LoadState loading={members.loading} error={members.error} openAuth={openAuth} />
      {!members.loading && !members.error && (
        list.length === 0 ? (
          <p className="state">Chưa có học viên nào đăng ký khóa học này.</p>
        ) : (
          <div className="table-wrap">
            <table className="members">
              <thead>
                <tr><th>Học viên</th><th>Email</th><th>Đã hoàn thành</th><th>Tiến độ</th></tr>
              </thead>
              <tbody>
                {list.map((m) => (
                  <tr key={m.studentId}>
                    <td>{m.fullName || "—"}</td>
                    <td>{m.email}</td>
                    <td>{m.completedLessons}/{m.totalLessons} bài</td>
                    <td>
                      <div className="progress__bar"><i style={{ width: m.progressPercentage + "%" }} /></div>
                      <small>{m.progressPercentage}%</small>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )
      )}
    </>
  );
}