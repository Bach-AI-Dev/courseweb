import { useEffect, useRef } from "react";
import ReactPlayer from "react-player";

const REPORT_EVERY = 10; // gửi tiến độ mỗi 10 giây xem

export default function VideoPlayer({ src, title, startAt = 0, onProgress }) {
  const playerRef = useRef(null);
  const pending = useRef(0); // giây đã xem nhưng chưa gửi
  const last = useRef(startAt); // mốc thời gian lần update trước
  const position = useRef(startAt); // vị trí hiện tại (giây)
  const report = useRef(onProgress);

  // Luôn giữ tham chiếu đến hàm onProgress mới nhất
  report.current = onProgress;

  const flush = () => {
    const delta = Math.floor(pending.current);
    if (delta < 1) return;
    pending.current -= delta;
    report.current?.({
      timeDelta: delta,
      lastPosition: Math.floor(position.current),
    });
  };

  // Gửi nốt tiến độ khi người dùng rời khỏi trang bài học
  useEffect(() => {
    return () => flush();
  }, []);

  // Đổi video (src khác) thì reset bộ đếm
  useEffect(() => {
    pending.current = 0;
    last.current = startAt;
    position.current = startAt;
  }, [src, startAt]);

  return (
    <div
      className="player"
      style={{ aspectRatio: "16/9", backgroundColor: "#000" }}
    >
      <ReactPlayer
        ref={playerRef}
        src={src} // v3: dùng "src" thay cho "url"
        title={title}
        controls
        width="100%"
        height="100%"
        // v3: thay cho onReady + seekTo()
        onLoadedMetadata={() => {
          const el = playerRef.current;
          if (startAt > 0 && el) {
            el.currentTime = startAt;
          }
        }}
        // v3: thay cho onProgress
        onTimeUpdate={(e) => {
          const t = e.currentTarget.currentTime;
          const d = t - last.current;

          // Chống tua video: chỉ cộng dồn khi khoảng cách giữa 2 lần
          // cập nhật nhỏ hơn 2 giây. Tua tới (d >= 2) hoặc lùi (d < 0) thì bỏ qua.
          if (d > 0 && d < 2) {
            pending.current += d;
          }

          last.current = t;
          position.current = t;

          // Đủ số giây quy định thì gửi dữ liệu về backend
          if (pending.current >= REPORT_EVERY) {
            flush();
          }
        }}
        onPause={flush}
        onEnded={flush}
      />
    </div>
  );
}
