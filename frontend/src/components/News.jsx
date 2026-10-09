import Section from "./Section";
import { NEWS } from "../data/content";

export default function News() {
  return (
    <Section id="tin-tuc" title="Tin tức" subtitle="Thông báo và chia sẻ mới nhất từ Mapstudy." alt>
      <div className="grid g3">
        {NEWS.map((n) => (
          <a className="card card--news" href="#tin-tuc" key={n.title}>
            <span className="tag">{n.date}</span>
            <h3>{n.title}</h3>
          </a>
        ))}
      </div>
    </Section>
  );
}
