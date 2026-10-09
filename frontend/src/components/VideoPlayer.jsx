import { useEffect, useRef } from "react";

function toYouTubeEmbed(url) {
  const m = url.match(/(?:youtu\.be\/|youtube\.com\/(?:watch\?v=|embed\/))([\w-]{11})/);
  return m ? `https://www.youtube.com/embed/${m[1]}` : null;
}

const REPORT_EVERY = 10; // gửi tiến độ mỗi 10 giây xem

/**
 * Với file video (.mp4...): đếm số giây thực sự xem (bỏ qua tua) và báo về backend
 * qua onProgress({ timeDelta, lastPosition }). Link YouTube chỉ phát, không theo dõi được tiến độ.
 */
export default function VideoPlayer({ src, title, startAt = 0, onProgress }) {
  const embed = toYouTubeEmbed(src);
  const pending = useRef(0); // giây đã xem nhưng chưa gửi
  const last = useRef(0); // mốc thời gian lần timeupdate trước
  const position = useRef(0); // vị trí hiện tại (giây)
  const report = useRef(onProgress);
  report.current = onProgress;

  const flush = () => {
    const delta = Math.floor(pending.current);
    if (delta < 1) return;
    pending.current -= delta;
    report.current?.({ timeDelta: delta, lastPosition: Math.floor(position.current) });
  };

  // Rời trang bài học thì gửi nốt phần chưa báo.
  useEffect(() => flush, []);

  if (embed) {
    return (
      <div className="player">
        <iframe src={embed} title={title} allow="encrypted-media; picture-in-picture" allowFullScreen />
      </div>
    );
  }

  return (
    <div className="player">
      <video
        src={src}
        controls
        preload="metadata"
        onLoadedMetadata={(e) => {
          const v = e.currentTarget;
          if (startAt > 0 && startAt < v.duration - 2) v.currentTime = startAt; // xem tiếp từ chỗ đã dừng
          last.current = v.currentTime;
          position.current = v.currentTime;
        }}
        onTimeUpdate={(e) => {
          const t = e.currentTarget.currentTime;
          const d = t - last.current;
          if (d > 0 && d < 2) pending.current += d; // d lớn = người dùng tua, không tính
          last.current = t;
          position.current = t;
          if (pending.current >= REPORT_EVERY) flush();
        }}
        onSeeked={(e) => (last.current = e.currentTarget.currentTime)}
        onPause={flush}
        onEnded={flush}
      />
    </div>
  );
}
