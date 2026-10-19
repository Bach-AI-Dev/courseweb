import { Link } from "react-router-dom";

export default function NotFoundPage() {
  return (
    <div className="page">
      <div className="wrap">
        <h1 className="page-title">Không tìm thấy trang</h1>
        <p className="page-sub">Khóa học hoặc bài học này không tồn tại.</p>
        <Link className="btn" to="/khoa-hoc">Về danh sách khóa học</Link>
      </div>
    </div>
  );
}
