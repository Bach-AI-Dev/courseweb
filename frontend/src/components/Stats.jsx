import { STATS } from "../data/content";

export default function Stats() {
  return (
    <div className="wrap stats">
      {STATS.map((s) => (
        <div className="stat" key={s.l}>
          <b>{s.n}</b>
          {s.l}
        </div>
      ))}
    </div>
  );
}
