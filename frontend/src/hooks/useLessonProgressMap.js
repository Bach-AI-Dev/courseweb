import { useEffect, useState } from "react";
import { getLessonProgress } from "../services/progressService";

// Backend chưa có API tiến độ cả khóa theo từng bài, nên gọi riêng từng bài video (song song).
export function useLessonProgressMap(lessons, enabled) {
  const [map, setMap] = useState({});
  const key = lessons.map((l) => l.id).join(",");

  useEffect(() => {
    if (!enabled || !lessons.length) {
      setMap({});
      return;
    }
    let alive = true;
    Promise.all(
      lessons
        .map((l) => getLessonProgress(l.id).then((p) => [l.id, p]).catch(() => null))
    ).then((entries) => alive && setMap(Object.fromEntries(entries.filter(Boolean))));
    return () => {
      alive = false;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [key, enabled]);

  return [map, setMap];
}
