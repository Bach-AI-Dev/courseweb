import { Link, useOutletContext } from "react-router-dom";
import Section from "./Section";
import CourseCover from "./CourseCover";
import { useAsync } from "../hooks/useAsync";
import { getCourses } from "../services/courseService";
import { formatPrice, isOpenCourse } from "../utils/format";

export default function Courses() {
  const { auth } = useOutletContext();
  const { data, loading, error } = useAsync(getCourses, [auth.user?.id]);
  const courses = (data || []).filter(isOpenCourse).slice(0, 4);

  return (
    <Section id="khoa-hoc" title="Khóa học" subtitle="Chọn khóa phù hợp với độ tuổi và mục tiêu của học viên." alt>
      {loading && <p className="state">Đang tải...</p>}
      {error && <p className="state">{error}</p>}
      <div className="grid g4">
        {courses.map((c) => (
          <Link key={c.id} to={`/khoa-hoc/${c.id}`} className="ccard">
            <CourseCover course={c} />
            <div className="ccard__body">
              <h3>{c.title}</h3>
              <p className="tag">{formatPrice(c.price)}</p>
            </div>
          </Link>
        ))}
      </div>
      <p style={{ marginTop: 28 }}>
        <Link className="btn" to="/khoa-hoc">Xem tất cả khóa học</Link>
      </p>
    </Section>
  );
}
