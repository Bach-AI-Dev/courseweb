import Section from "./Section";
import { ABOUT } from "../data/content";

export default function About() {
  return (
    <Section
      id="gioi-thieu"
      title="Giới thiệu"
      subtitle="Chúng tôi xây dựng lộ trình học theo từng học sinh: xác định điểm xuất phát, đặt mục tiêu rõ ràng và đồng hành đến khi đạt kết quả."
    >
      <div className="grid g3">
        {ABOUT.map((a) => (
          <div className="card" key={a.title}>
            <h3>{a.title}</h3>
            <p>{a.desc}</p>
          </div>
        ))}
      </div>
    </Section>
  );
}
