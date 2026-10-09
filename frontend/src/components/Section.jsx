export default function Section({ id, title, subtitle, alt = false, children }) {
  return (
    <section id={id} className={"section" + (alt ? " section--alt" : "")}>
      <div className="wrap">
        <h2>{title}</h2>
        {subtitle && <p className="sub">{subtitle}</p>}
        {children}
      </div>
    </section>
  );
}
