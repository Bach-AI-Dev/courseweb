import { useEffect, useState } from "react";
import { useAsync } from "../hooks/useAsync";
import { getCourseMembersProgress } from "../services/progressService";
import LoadState from "./LoadState";

// Bảng tiến độ của tất cả học viên trong khóa học (ADMIN, TEACHER).
export default function CourseMembersProgress({ courseId, openAuth }) {
    const [keyword, setKeyword] = useState(""); // nội dung đang gõ
    const [search, setSearch] = useState("");   // từ khóa gửi lên BE (đã chờ người dùng gõ xong)

    // Đợi 300ms sau lần gõ cuối rồi mới gọi BE, tránh gọi API sau mỗi phím
    useEffect(() => {
        const t = setTimeout(() => setSearch(keyword.trim()), 300);
        return () => clearTimeout(t);
    }, [keyword]);

    const members = useAsync(() => getCourseMembersProgress(courseId, search), [courseId, search]);
    const list = members.data || [];

    return (
        <>
            <div className="section-head">
                <h2 className="section-title">Tiến độ học viên{members.data ? ` (${list.length})` : ""}</h2>
                <button className="btn alt" onClick={members.reload}>Làm mới</button>
            </div>

            {/* Ô tìm kiếm luôn hiển thị để không mất focus khi đang tải kết quả */}
            <input
                className="members-search"
                type="search"
                placeholder="Tìm theo tên hoặc email học viên..."
                value={keyword}
                onChange={(e) => setKeyword(e.target.value)}
            />

            <LoadState loading={members.loading} error={members.error} openAuth={openAuth} />
            {!members.error && members.data && (
                list.length === 0 ? (
                    <p className="state">
                        {search ? `Không tìm thấy học viên nào khớp “${search}”.` : "Chưa có học viên nào đăng ký khóa học này."}
                    </p>
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