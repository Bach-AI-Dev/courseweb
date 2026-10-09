import { useState } from "react";
import { Link, useOutletContext, useParams } from "react-router-dom";
import { useAsync } from "../hooks/useAsync";
import { useLessonProgressMap } from "../hooks/useLessonProgressMap";
import { getCourse, getLesson, getLessons } from "../services/courseService";
import { isEnrolled } from "../services/enrollmentService";
import { getLessonProgress, updateProgress } from "../services/progressService";
import { formatDuration, isHttpLink } from "../utils/format";
import { isStaff } from "../utils/roles";
import VideoPlayer from "../components/VideoPlayer";
import LessonList from "../components/LessonList";
import LoadState from "../components/LoadState";

export default function LessonPage() {
  const { courseId, lessonId } = useParams();
  const { auth, openAuth } = useOutletContext();
  const uid = auth.user?.id;
  const staff = isStaff(auth.user);
  const student = auth.user?.role === "STUDENT";

  const course = useAsync(() => getCourse(courseId), [courseId, uid]);
  const lessons = useAsync(() => getLessons(courseId), [courseId, uid]);
  const enrolled = useAsync(() => (student ? isEnrolled(courseId) : Promise.resolve(false)), [courseId, uid, student]);

  // ADMIN/TEACHER xem thẳng; học viên phải đăng ký khóa (backend cũng kiểm tra lại)
  const canWatch = Boolean(uid && (staff || enrolled.data));
  const tracking = Boolean(student && enrolled.data); // chỉ học viên mới lưu tiến độ

  const lesson = useAsync(() => (canWatch ? getLesson(lessonId) : Promise.resolve(null)), [lessonId, uid, canWatch]);
  const saved = useAsync(() => (tracking ? getLessonProgress(lessonId) : Promise.resolve(null)), [lessonId, uid, tracking]);

  const lessonList = lessons.data || [];
  const [progressMap, setProgressMap] = useLessonProgressMap(lessonList, tracking);
  const [reportError, setReportError] = useState("");

  const waitingLesson = canWatch && !lesson.data && !lesson.error;
  const loading = course.loading || lessons.loading || enrolled.loading || lesson.loading || saved.loading || waitingLesson;
  const error = course.error || lessons.error || lesson.error;
  if (loading || error) {
    return <div className="page"><div className="wrap"><LoadState loading={loading} error={error} openAuth={openAuth} /></div></div>;
  }

  // Chưa xem được thì chỉ có tên bài từ danh sách (danh sách không kèm link video)
  const l = lesson.data || lessonList.find((x) => x.id === lessonId);
  if (!l) return <div className="page"><div className="wrap"><p className="state state--error">Không tìm thấy bài học.</p></div></div>;

  const index = lessonList.findIndex((x) => x.id === l.id);
  const prev = lessonList[index - 1];
  const next = lessonList[index + 1];
  const base = `/khoa-hoc/${courseId}/bai-hoc/`;
  const live = tracking ? progressMap[l.id] || saved.data : null;

  // Mỗi lần trình phát báo tiến độ, backend tính lại % và trạng thái hoàn thành.
  const onProgress = async (payload) => {
    try {
      const res = await updateProgress({ lessonId: l.id, ...payload });
      setProgressMap((m) => ({ ...m, [l.id]: res }));
      setReportError("");
    } catch (e) {
      setReportError(e.message);
    }
  };

  return (
    <div className="page">
      <div className="wrap">
        <nav className="crumbs">
          <Link to="/khoa-hoc">Khóa học</Link> / <Link to={`/khoa-hoc/${courseId}`}>{course.data.title}</Link> / {l.name}
        </nav>

        <div className="lesson-layout">
          <div>
            {!uid ? (
              <div className="state">
                <p>Vui lòng đăng nhập để xem bài học.</p>
                <button className="btn" onClick={() => openAuth("login")}>Đăng nhập</button>
              </div>
            ) : !canWatch ? (
              <div className="state">
                <p>Bạn cần đăng ký khóa học này để xem bài học.</p>
                <Link className="btn" to={`/khoa-hoc/${courseId}`}>Về trang khóa học</Link>
              </div>
            ) : l.videoUrl ? (
              <VideoPlayer
                key={l.id}
                src={l.videoUrl}
                title={l.name}
                startAt={saved.data?.lastPosition || 0}
                onProgress={tracking ? onProgress : undefined}
              />
            ) : (
              <div className="state">Bài học chưa có video.</div>
            )}

            <h1 className="page-title lesson-title">{l.name}</h1>
            <p className="page-sub">
              Video · {formatDuration(l.duration)}
              {live?.isCompleted ? " · ✓ Đã hoàn thành" : live?.completionPercentage > 0 ? ` · Đã xem ${live.completionPercentage}%` : ""}
            </p>
            {reportError && <p className="state state--error">{reportError}</p>}

            <div className="lesson-actions">
              {prev && <Link className="btn alt" to={base + prev.id}>← Bài trước</Link>}
              {next && <Link className="btn" to={base + next.id}>Bài tiếp →</Link>}
            </div>

            {canWatch && (
              <section className="assignment">
                <h2 className="section-title">Bài tập</h2>
                {isHttpLink(l.assignmentUrl) ? (
                  <a className="btn" href={l.assignmentUrl} target="_blank" rel="noopener noreferrer">Mở bài tập ↗</a>
                ) : (
                  <p className="state">Bài học này chưa có bài tập.</p>
                )}
              </section>
            )}
          </div>

          <aside className="lesson-side">
            <h2 className="side-title">{course.data.title}</h2>
            <LessonList courseId={courseId} lessons={lessonList} progressMap={progressMap} activeId={l.id} />
          </aside>
        </div>
      </div>
    </div>
  );
}
