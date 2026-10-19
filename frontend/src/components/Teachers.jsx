import Section from "./Section";
import { TEACHERS } from "../data/content";

export default function Teachers() {
  return (
    <Section id="giang-vien" title="Giảng viên" subtitle="Đội ngũ giáo viên có chuyên môn vững và kinh nghiệm làm việc với học sinh.">
      <div className="grid g3">
        {TEACHERS.map((t) => (
          <div className="card" key={t.name}>
            <div className="avatar">{t.name.split(" ").pop()[0]}</div>
            <h3>{t.name}</h3>
            <p>{t.role}</p>
          </div>
        ))}
      </div>
    </Section>
  );
}
