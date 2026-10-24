# 🎓 E-Learning Course Web Application

Hệ thống nền tảng học trực tuyến hỗ trợ quản lý khóa học, bài giảng và theo dõi tiến độ học tập của học viên theo thời gian thực. Dự án được xây dựng theo kiến trúc phân tầng RESTful API với Backend bằng Spring Boot và Frontend bằng React.

---

## 📌 Tính năng nổi bật

- **Xác thực & Bảo mật (Authentication & Authorization):**
  - Đăng ký, đăng nhập an toàn sử dụng **Spring Security** và **JSON Web Tokens (JWT)**.
  - Phân quyền người dùng (Học viên / Giảng viên / Quản trị viên).

- **Quản lý Khóa học & Bài giảng (Course Management):**
  - Hiển thị danh mục khóa học, nội dung chi tiết bài học và video giảng dạy.
  - Tìm kiếm và lọc khóa học theo chủ đề.

- **Đồng bộ tiến độ học tập theo thời gian thực (Real-time Progress Tracking):**
  - Tích hợp trình phát video với giao diện React (`ReactPlayer`).
  - Tự động ghi nhận thời gian xem thực tế, lưu vết vị trí dừng video gần nhất (`last position`) và cập nhật tỷ lệ hoàn thành bài học về server qua REST API.

---

## 🛠️ Công nghệ sử dụng

### Backend
- **Ngôn ngữ:** Java 21
- **Framework:** Spring Boot, Spring Data JPA, Spring Security
- **Xác thực:** JWT (JSON Web Token)
- **Cơ sở dữ liệu:** MySQL (`courseweb_db.sql`)
- **Quản lý thư viện / Build:** Maven

### Frontend
- **Thư viện chính:** React (JavaScript/TypeScript)
- **Công cụ phát video:** ReactPlayer
- **Giao tiếp API:** Axios / Fetch API

### Quản lý phiên bản & Quy trình
- **Version Control:** Git, GitHub (Git Flow)

---

## 📂 Cấu trúc dự án

```text
courseweb/
├── backend/          # Mã nguồn server Spring Boot (Controller, Service, Repository, DTO)
├── frontend/         # Giao diện người dùng React (Components, Pages, Services)
├── courseweb_db.sql  # File sao lưu lược đồ và dữ liệu mẫu cơ sở dữ liệu MySQL
└── README.md
