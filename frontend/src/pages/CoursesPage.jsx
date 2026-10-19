import { useState } from "react";
import { Link, useOutletContext } from "react-router-dom";
import { useAsync } from "../hooks/useAsync";
import { getCourses, getCategories } from "../services/courseService";
import { getUsers } from "../services/userService";
import { formatPrice, isOpenCourse } from "../utils/format";
import { isStaff } from "../utils/roles";
import CourseCover from "../components/CourseCover";
import CategoryManagerModal from "../components/CategoryManagerModal";
import CourseFormModal from "../components/CourseFormModal";
import LoadState from "../components/LoadState";

export default function CoursesPage() {
  const { auth, openAuth } = useOutletContext();
  const staff = isStaff(auth.user);
  const [categoryId, setCategoryId] = useState("");
  const [adding, setAdding] = useState(false);
  const [managingCategories, setManagingCategories] = useState(false);
  const courses = useAsync(getCourses, [auth.user?.id]);
  const categories = useAsync(getCategories, [auth.user?.id]);
  // Chỉ ADMIN được xem danh sách người dùng để chọn giáo viên phụ trách
  const users = useAsync(() => (auth.user?.role === "ADMIN" ? getUsers() : Promise.resolve([])), [auth.user?.id]);
  const teachers = (users.data || []).filter((u) => u.role === "TEACHER" || u.role === "ADMIN");

  // Học viên chỉ thấy khóa đang mở; ADMIN/TEACHER thấy cả khóa nháp để quản lý
  const list = (courses.data || []).filter((c) => (staff || isOpenCourse(c)) && (!categoryId || c.categoryId === categoryId));

  return (
    <div className="page">
      <div className="wrap">
        <div className="section-head">
          <h1 className="page-title">Khóa học</h1>
          {staff && <button className="btn" onClick={() => setAdding(true)}>+ Thêm khóa học</button>}
        </div>
        <p className="page-sub">Chọn một khóa học để xem danh sách bài học và video.</p>

        {(categories.data?.length > 0 || staff) && (
          <div className="chips">
            {categories.data?.length > 0 && (
              <button className={!categoryId ? "on" : ""} onClick={() => setCategoryId("")}>Tất cả</button>
            )}
            {(categories.data || []).map((c) => (
              <button key={c.id} className={categoryId === c.id ? "on" : ""} onClick={() => setCategoryId(c.id)}>{c.name}</button>
            ))}
            {staff && <button className="chips__add" onClick={() => setManagingCategories(true)}>Quản lý danh mục</button>}
          </div>
        )}

        <LoadState loading={courses.loading} error={courses.error} openAuth={openAuth} />
        {!courses.loading && !courses.error && list.length === 0 && <p className="state">Chưa có khóa học nào.</p>}

        <div className="grid g3">
          {list.map((c) => (
            <Link key={c.id} to={`/khoa-hoc/${c.id}`} className="ccard">
              <CourseCover course={c} />
              <div className="ccard__body">
                <h3>{c.title}</h3>
                <p className="tag">
                  {formatPrice(c.price)}
                  {staff && !isOpenCourse(c) && <span className="badge">{c.status === "ARCHIVED" ? "Lưu trữ" : "Nháp"}</span>}
                </p>
              </div>
            </Link>
          ))}
        </div>
      </div>

      {adding && (
        <CourseFormModal
          categories={categories.data || []}
          teachers={teachers}
          currentUser={auth.user}
          onClose={() => setAdding(false)}
          onSaved={() => { setAdding(false); courses.reload(); }}
          onCategoryAdded={categories.reload}
        />
      )}
      {managingCategories && (
        <CategoryManagerModal
          categories={categories.data || []}
          isAdmin={auth.user?.role === "ADMIN"}
          onClose={() => setManagingCategories(false)}
          onChanged={() => { setCategoryId(""); categories.reload(); courses.reload(); }}
        />
      )}
    </div>
  );
}
