import { useEffect, useState } from "react";
import { getToken, clearToken } from "../services/api";
import * as authService from "../services/authService";

// Hàm login/register trả về chuỗi lỗi, hoặc null nếu thành công.
export function useAuth() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(Boolean(getToken()));

  // Tải lại trang: có token thì lấy lại thông tin người dùng.
  useEffect(() => {
    if (!getToken()) return;
    authService
      .getMyInfo()
      .then(setUser)
      .catch(() => clearToken())
      .finally(() => setLoading(false));
  }, []);

  // Token hết hạn (api.js phát sự kiện) thì đăng xuất.
  useEffect(() => {
    const onExpired = () => setUser(null);
    window.addEventListener("auth:expired", onExpired);
    return () => window.removeEventListener("auth:expired", onExpired);
  }, []);

  const login = async ({ username, password }) => {
    try {
      setUser(await authService.login({ username: username.trim(), password }));
      return null;
    } catch (e) {
      return e.message;
    }
  };

  const register = async (form) => {
    if (form.password !== form.confirm) return "Mật khẩu nhập lại không khớp.";
    try {
      const { username, password, email, fullName, phone } = form;
      await authService.register({ username: username.trim(), password, email: email.trim(), fullName: fullName.trim(), phone: phone.trim() });
      setUser(await authService.login({ username: username.trim(), password }));
      return null;
    } catch (e) {
      return e.message;
    }
  };

  const logout = () => {
    authService.logout();
    setUser(null);
  };

  return { user, loading, login, register, logout };
}
