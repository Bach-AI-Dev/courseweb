import { api } from "./api";

// ---- Dành cho mọi người dùng đã đăng nhập ----
// Cập nhật thông tin của chính mình. Backend ghi đè email, fullName, phone bằng đúng giá trị gửi lên
// (trường nào thiếu sẽ thành null), nên luôn gửi đủ cả ba. Chỉ gửi password khi muốn đổi mật khẩu.
export const updateMyInfo = (body) => api("/users/update", { method: "PUT", body });

// ---- Chỉ ADMIN ----
export const getUsers = () => api("/users/admin/list");
export const getUser = (userId) => api(`/users/admin/${userId}`);
export const deleteUser = (userId) => api(`/users/admin/${userId}`, { method: "DELETE" });
export const createTeacher = (body) => api("/auth/admin/teacher", { method: "POST", body });
