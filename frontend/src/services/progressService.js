import { api } from "./api";

// timeDelta: số giây đã xem từ lần báo trước; lastPosition: vị trí hiện tại (giây).
export const updateProgress = ({ lessonId, timeDelta, lastPosition }) =>
  api("/v1/progress/update", { method: "POST", body: { lessonId, timeDelta, lastPosition } });

export const getCourseProgress = (courseId) => api(`/v1/progress/courses/${courseId}/progress`);
export const getLessonProgress = (lessonId) => api(`/v1/progress/lessons/${lessonId}/progress`);
