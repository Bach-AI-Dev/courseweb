import { api } from "./api";

// Chỉ ADMIN: dùng để chọn giáo viên phụ trách khi tạo khóa học.
export const getUsers = () => api("/users");
