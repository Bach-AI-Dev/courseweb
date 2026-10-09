# CourseWeb (React + Vite) nối backend Spring Boot

```bash
npm install
npm run dev        # frontend http://localhost:5173, proxy /api -> http://localhost:8080
```

Chạy backend trước (`courseweb`, cổng 8080, SQL Server). Backend chưa bật CORS nên khi dev phải đi qua proxy của Vite.

## Cấu trúc nối API

| Lớp | File |
|---|---|
| Gọi HTTP, gắn JWT, bóc `ApiResponse` | `src/services/api.js` |
| Đăng nhập / đăng ký / thông tin cá nhân | `src/services/authService.js`, `src/hooks/useAuth.js` |
| Khóa học, bài học, danh mục | `src/services/courseService.js` |
| Đăng ký học, khóa học của tôi | `src/services/enrollmentService.js` |
| Tiến độ xem video | `src/services/progressService.js`, `src/components/VideoPlayer.jsx` |

## Các trang

| Đường dẫn | Trang | API dùng |
|---|---|---|
| `/` | Trang chủ (4 khóa học đầu) | `GET /api/courses` |
| `/khoa-hoc` | Danh sách + lọc danh mục | `GET /api/courses`, `GET /api/categories` |
| `/khoa-hoc/:courseId` | Chi tiết khóa, đăng ký học, tiến độ | courses, lessons, enrollment-status, enroll, progress |
| `/khoa-hoc/:courseId/bai-hoc/:lessonId` | Xem video, lưu tiến độ | lessons, progress/update |
| `/khoa-hoc-cua-toi` | Khóa đã đăng ký, hủy đăng ký | `GET /api/users/me/courses`, `DELETE .../enrollment` |

Ghi chú: JWT lưu trong localStorage (key `cw_token`), hết hạn sau 1 giờ.

## Phân quyền trên giao diện

| Vai trò | Quyền |
|---|---|
| Chưa đăng nhập | Xem khóa học và danh sách bài học (không có link video) |
| STUDENT | Đăng ký học, xem video của khóa đã đăng ký, lưu tiến độ, hủy đăng ký |
| TEACHER | Không cần đăng ký học. Thêm/sửa/xóa khóa học và bài học |
| ADMIN | Như TEACHER, thêm quyền chọn giáo viên phụ trách khi tạo khóa |

Backend là nơi kiểm tra quyền thật sự (`@PreAuthorize`). Giao diện chỉ ẩn/hiện nút cho phù hợp.

## Bài học: video + bài tập

Mỗi bài học có video (bắt buộc: link và thời lượng giây) và link bài tập (tùy chọn, http/https).
Link video và link bài tập chỉ hiện với ADMIN/TEACHER hoặc học viên đã đăng ký khóa.
Cột `assignment_url` của bảng `lessons` cần có trước khi chạy backend (`ddl-auto=validate`).
