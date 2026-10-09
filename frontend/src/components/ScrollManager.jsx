import { useEffect } from "react";
import { useLocation } from "react-router-dom";

// Đổi trang thì cuộn lên đầu; link dạng /#muc thì cuộn tới đúng mục.
export default function ScrollManager() {
  const { pathname, hash } = useLocation();

  useEffect(() => {
    if (hash) {
      const el = document.getElementById(hash.slice(1));
      if (el) {
        el.scrollIntoView();
        return;
      }
    }
    window.scrollTo(0, 0);
  }, [pathname, hash]);

  return null;
}
