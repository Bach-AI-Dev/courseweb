import { useState } from "react";
import { Link, useNavigate, useOutletContext, useParams } from "react-router-dom";
import { useAsync } from "../hooks/useAsync";
import { useLessonProgressMap } from "../hooks/useLessonProgressMap";
import { deleteCourse, deleteLesson, getCourse, getLessons } from "../services/courseService";
import { enroll, isEnrolled } from "../services/enrollmentService";
import { getCourseProgress } from "../services/progressService";
import { formatPrice } from "../utils/format";
import { isStaff } from "../utils/roles";
import CourseCover from "../components/CourseCover";
import CourseFormModal from "../components/CourseFormModal";
import LessonFormModal from "../components/LessonFormModal";
import LessonList from "../components/LessonList";
import LoadState from "../components/LoadState";

export default function CourseDetailPage() {
  const { courseId } = useParams();
  const { auth, openAuth } = useOutletContext();
  const navigate = useNavigate();
  const uid = auth.user?.id;
  const staff = isStaff(auth.user); // ADMIN, TEACHER: quản lý nội dung, không cần đăng ký học
  const student = auth.user?.role === "STUDENT";

  const course = useAsync(() => getCourse(courseId), [courseId, uid]);
  const lessons = useAsync(() => getLessons(courseId), [courseId, uid]);
  const enrolled = useAsync(() => (student ? isEnrolled(courseId) : Promise.resolve(false)), [courseId, uid, student]);
  const tracking = Boolean(student && enrolled.data);
  const progress = useAsync(() => (tracking ? getCourseProgress(courseId) : Promise.resolve(null)), [courseId, uid, tracking]);
  const lessonList = lessons.data || [];
  const [progressMap] = useLessonProgressMap(lessonList, tracking);

  const [busy, setBusy] = useState(false);
  const [actionError, setActionError] = useState("");
  const [editingCourse, setEditingCourse] = useState(false);
  const [lessonModal, setLessonModal] = useState(null); // null | { lesson: bài cũ hoặc null }

  const handleEnroll = async () => {
    setBusy(true);
    setActionError("");
    try {
      await enroll(courseId);
      enrolled.reload();
    } catch (e) {
      setActionError(e.message);
    }
    setBusy(false);
  };

  const handleDeleteCourse = async () => {
    if (!window.confirm("Xóa khóa học này? Chỉ xóa được khi khóa chưa có bài học và chưa có học viên.")) return;
    setActionError("");
    try {
      await deleteCourse(courseId);
      navigate("/khoa-hoc");
    } catch (e) {
      setActionError(e.message);
    }
  };

  const handleDeleteLesson = async (lesson) => {
    if (!window.confirm(`Xóa bài học "${lesson.name}"?`)) return;
    setActionError("");
    try {
      await deleteLesson(lesson.id);
      lessons.reload();
    } catch (e) {
      setActionError(e.message);
    }
  };

  if (course.loading || course.error) {
    return <div className="page"><div className="wrap"><LoadState loading={course.loading} error={course.error} openAuth={openAuth} /></div></div>;
  }

  const c = course.data;
  const firstLesson = lessonList[0];
  const nextOrder = lessonList.reduce((max, l) => Math.max(max, l.lessonOrder || 0), 0) + 1;

  return (
    <div className="page">
      <div className="wrap">
        <nav className="crumbs">
          <Link to="/">Trang chủ</Link> / <Link to="/khoa-hoc">Khóa học</Link> / {c.title}
        </nav>

        <div className="course-head">
          <CourseCover course={c} />
          <div>
            <h1 className="page-title">{c.title}</h1>
            <p className="tag">{formatPrice(c.price)} · {lessonList.length} bài học</p>
            {c.description && <p>{c.description}</p>}

            {tracking && progress.data && (
              <div className="progress">
                <div className="progress__bar"><i style={{ width: progress.data.progressPercentage + "%" }} /></div>
                <small>Đã hoàn thành {progress.data.completedLessons}/{progress.data.totalLessons} bài ({progress.data.progressPercentage}%)</small>
              </div>
            )}

            <div className="actions">
              {!uid ? (
                <button className="btn" onClick={() => openAuth("login")}>Đăng nhập để đăng ký học</button>
              ) : staff ? (
                <>
                  {firstLesson && <Link className="btn" to={`/khoa-hoc/${courseId}/bai-hoc/${firstLesson.id}`}>Vào xem</Link>}
                  <button className="btn alt" onClick={() => setEditingCourse(true)}>Sửa khóa học</button>
                  <button className="btn danger" onClick={handleDeleteCourse}>Xóa khóa học</button>
                </>
              ) : enrolled.data ? (
                firstLesson && <Link className="btn" to={`/khoa-hoc/${courseId}/bai-hoc/${firstLesson.id}`}>Vào học</Link>
              ) : (
                <button className="btn" onClick={handleEnroll} disabled={busy}>{busy ? "Đang đăng ký..." : "Đăng ký học"}</button>
              )}
            </div>
            {actionError && <p className="state state--error">{actionError}</p>}
          </div>
        </div>

        <div className="section-head">
          <h2 className="section-title">Danh sách bài học</h2>
          {staff && <button className="btn" onClick={() => setLessonModal({ lesson: null })}>+ Thêm bài học</button>}
        </div>
        <LoadState loading={lessons.loading} error={lessons.error} openAuth={openAuth} />
        {!lessons.loading && !lessons.error && (
          <LessonList
            courseId={courseId}
            lessons={lessonList}
            progressMap={progressMap}
            onEdit={staff ? (l) => setLessonModal({ lesson: l }) : undefined}
            onDelete={staff ? handleDeleteLesson : undefined}
          />
        )}
      </div>

      {editingCourse && (
        <CourseFormModal
          course={c}
          currentUser={auth.user}
          onClose={() => setEditingCourse(false)}
          onSaved={() => { setEditingCourse(false); course.reload(); }}
        />
      )}
      {lessonModal && (
        <LessonFormModal
          courseId={courseId}
          lesson={lessonModal.lesson}
          nextOrder={nextOrder}
          onClose={() => setLessonModal(null)}
          onSaved={() => { setLessonModal(null); lessons.reload(); }}
        />
      )}
    </div>
  );
}
