import { read, write } from "../utils/storage";

// Mặc định gọi "/api" (Vite proxy sang Spring Boot ở cổng 8080 khi chạy dev).
// Khi deploy, đặt VITE_API_URL=https://ten-mien-backend/api trong .env
const BASE_URL = import.meta.env.VITE_API_URL || "/api";
const TOKEN_KEY = "cw_token";

export const getToken = () => read(TOKEN_KEY, null);
export const setToken = (token) => write(TOKEN_KEY, token);
export const clearToken = () => write(TOKEN_KEY, null);

/**
 * Gọi backend. Tự gắn JWT, bóc lớp ApiResponse ({code, message, result}) nếu có,
 * và ném Error(message) để giao diện hiển thị.
 */
export async function api(path, { method = "GET", body } = {}) {
  const token = getToken();
  const res = await fetch(`${BASE_URL}${path}`, {
    method,
    headers: { "Content-Type": "application/json", ...(token ? { Authorization: `Bearer ${token}` } : {}) },
    body: body ? JSON.stringify(body) : undefined,
  });

  const text = await res.text();
  let data = null;
  try {
    data = text ? JSON.parse(text) : null;
  } catch {
    data = text; // một số API trả chuỗi thường (vd: "Deleted lesson successfully")
  }

  if (res.status === 401) {
    clearToken();
    if (token) window.dispatchEvent(new Event("auth:expired"));
    throw new Error(token ? "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại." : "Vui lòng đăng nhập để tiếp tục.");
  }
  if (res.status === 403) throw new Error("Bạn không có quyền thực hiện thao tác này.");
  if (!res.ok) throw new Error(data?.message || `Lỗi ${res.status}`);

  return data && typeof data === "object" && "result" in data ? data.result : data;
}
