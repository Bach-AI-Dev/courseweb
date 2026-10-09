// 125 -> "2:05", 3725 -> "1:02:05"
export function formatDuration(seconds) {
  if (!seconds && seconds !== 0) return "";
  const h = Math.floor(seconds / 3600);
  const m = Math.floor((seconds % 3600) / 60);
  const s = Math.floor(seconds % 60);
  const pad = (n) => String(n).padStart(2, "0");
  return h > 0 ? `${h}:${pad(m)}:${pad(s)}` : `${m}:${pad(s)}`;
}

export const formatPrice = (price) =>
  !price || Number(price) === 0 ? "Miễn phí" : `${new Intl.NumberFormat("vi-VN").format(price)}đ`;

export const formatDate = (iso) => (iso ? new Date(iso).toLocaleDateString("vi-VN") : "");

// Backend không có màu bìa, nên sinh màu từ id để mỗi khóa một màu cố định.
export function hueFromId(id = "") {
  let h = 0;
  for (const ch of id) h = (h * 31 + ch.charCodeAt(0)) % 360;
  return h;
}

// CourseStatus của backend: DRAFT, PUBLISHED, ARCHIVED. Chỉ PUBLISHED mới mở cho học viên.
export const isOpenCourse = (course) => course.status === "PUBLISHED";

// Chỉ cho phép link http(s) để tránh link độc hại dạng javascript:
export const isHttpLink = (url) => /^https?:\/\//i.test(url || "");
