import { api } from "./api";

export const enroll = (courseId) => api(`/courses/${courseId}/enroll`, { method: "POST" });
export const cancelEnrollment = (courseId) => api(`/courses/${courseId}/enrollment`, { method: "DELETE" });
export const isEnrolled = (courseId) => api(`/courses/${courseId}/enrollment-status`);
export const getMyCourses = () => api("/users/me/courses");
