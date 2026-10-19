import { api } from "./api";

export const getCourses = () => api("/courses");
export const getCourse = (courseId) => api(`/courses/${courseId}`);
export const getLessons = (courseId) => api(`/courses/${courseId}/lessons`);
export const getLesson = (lessonId) => api(`/lessons/${lessonId}`);
export const getCategories = () => api("/categories");

// Chỉ ADMIN và TEACHER gọi được các hàm dưới đây (backend trả 403 nếu không đủ quyền).
export const createCourse = (body) => api("/courses", { method: "POST", body });
export const updateCourse = (courseId, body) => api(`/courses/${courseId}`, { method: "PUT", body });
export const deleteCourse = (courseId) => api(`/courses/${courseId}`, { method: "DELETE" });
export const createLesson = (courseId, body) => api(`/courses/${courseId}/lessons`, { method: "POST", body });
export const updateLesson = (lessonId, body) => api(`/lessons/${lessonId}`, { method: "PUT", body });
export const deleteLesson = (lessonId) => api(`/lessons/${lessonId}`, { method: "DELETE" });

// ADMIN và TEACHER tạo được danh mục mới (backend kiểm tra quyền)
export const createCategory = (body) => api("/categories", { method: "POST", body });
export const updateCategory = (categoryId, body) => api(`/categories/${categoryId}`, { method: "PUT", body });
export const deleteCategory = (categoryId) => api(`/categories/${categoryId}`, { method: "DELETE" });
