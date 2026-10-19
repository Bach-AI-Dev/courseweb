import { useState } from "react";
import { Link, useOutletContext } from "react-router-dom";
import { useAsync } from "../hooks/useAsync";
import { getCourses } from "../services/courseService";
import { cancelEnrollment, getMyCourses } from "../services/enrollmentService";
import { formatDate } from "../utils/format";
import { isStaff } from "../utils/roles";
import LoadState from "../components/LoadState";

const STATUS = { ACTIVE: "Đang học", COMPLETED: "Đã hoàn thành", CANCELED: "Đã hủy" };

export default function MyCoursesPage() {
  const { auth, openAuth } = useOutletContext();
  const uid = auth.user?.id;
  const staff = isStaff(auth.user);
  const mine = useAsync(() => (uid && !staff ? getMyCourses() : Promise.resolve([])), [uid]);
  const courses = useAsync(() => (uid && !staff ? getCourses() : Promise.resolve([])), [uid]);
  const [error, setError] = useState("");

  if (auth.loading) return <div className="page"><div className="wrap"><p className="state">Đang tải...</p></div></div>;
  if (!uid) {
    return (
      <div className="page"><div className="wrap">
        <div className="state">
          <p>Vui lòng đăng nhập để xem khóa học của bạn.</p>
          <button className="btn" onClick={() => openAuth("login")}>Đăng nhập</button>
        </div>
      </div></div>
    );
  }

  if (staff) {
    return (
      <div className="page"><div className="wrap">
        <p className="state">Tài khoản giáo viên/quản trị viên không cần đăng ký khóa học. <Link to="/khoa-hoc">Quản lý khóa học</Link></p>
      </div></div>
    );
  }

  // EnrollmentResponse của backend chưa có courseId nên tạm nối với khóa học qua tiêu đề.
  const byTitle = new Map((courses.data || []).map((c) => [c.title, c]));

  const cancel = async (courseId) => {
    setError("");
    try {
      await cancelEnrollment(courseId);
      mine.reload();
    } catch (e) {
      setError(e.message);
    }
  };

  return (
    <div className="page">
      <div className="wrap">
        <h1 className="page-title">Khóa học của tôi</h1>
        <LoadState loading={mine.loading} error={mine.error} openAuth={openAuth} />
        {error && <p className="state state--error">{error}</p>}
        {!mine.loading && !mine.error && mine.data.length === 0 && (
          <p className="state">Bạn chưa đăng ký khóa học nào. <Link to="/khoa-hoc">Xem khóa học</Link></p>
        )}
        <div className="grid g3">
          {(mine.data || []).map((e) => {
            const course = byTitle.get(e.courseTitle);
            return (
              <div className="card" key={e.id}>
                <span className="tag">{STATUS[e.status] || e.status} · {formatDate(e.enrollDate)}</span>
                <h3>{e.courseTitle}</h3>
                {course && (
                  <div className="actions">
                    <Link className="btn" to={`/khoa-hoc/${course.id}`}>Vào học</Link>
                    {e.status === "ACTIVE" && <button className="btn alt" onClick={() => cancel(course.id)}>Hủy đăng ký</button>}
                  </div>
                )}
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}
