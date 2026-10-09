import { api, setToken, clearToken } from "./api";

export const getMyInfo = () => api("/users/my-info");

// Đăng nhập bằng username (không phải email), lưu JWT rồi lấy thông tin người dùng.
export async function login({ username, password }) {
  const { token } = await api("/auth/login", { method: "POST", body: { username, password } });
  setToken(token);
  return getMyInfo();
}

export const register = (data) => api("/auth/register", { method: "POST", body: data });

export const logout = () => clearToken();
