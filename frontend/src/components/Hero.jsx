import { Link } from "react-router-dom";
import "./Hero.css";

const CONTOURS = [0, 1, 2, 3, 4, 5, 6, 7];

export default function Hero() {
  return (
    <div className="hero" id="top">
      <svg viewBox="0 0 800 400" preserveAspectRatio="xMidYMid slice" fill="none" stroke="#5fb3c0" strokeWidth="1" aria-hidden="true">
        {CONTOURS.map((i) => (
          <path key={i} d={`M-20 ${300 - i * 22} C 160 ${220 - i * 24}, 300 ${360 - i * 26}, 460 ${240 - i * 22} S 720 ${180 - i * 14}, 840 ${120 - i * 10}`} />
        ))}
      </svg>
      <div className="wrap hero__content">
        <h1>Định vị tri thức – dẫn lối tư duy</h1>
        <p>CourseWeb giúp học sinh hiểu bản chất, biết cách học và tự tin tìm ra hướng đi của riêng mình.</p>
        <div className="hero__actions">
          <Link className="btn" to="/khoa-hoc">Xem khóa học</Link>
          <a className="btn alt" href="#gioi-thieu">Tìm hiểu thêm</a>
        </div>
      </div>
    </div>
  );
}
