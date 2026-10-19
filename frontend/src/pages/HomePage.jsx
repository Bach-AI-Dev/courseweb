import Hero from "../components/Hero";
import Stats from "../components/Stats";
import About from "../components/About";
import Courses from "../components/Courses";
import Teachers from "../components/Teachers";
import News from "../components/News";

export default function HomePage() {
  return (
    <>
      <Hero />
      <Stats />
      <About />
      <Courses />
      <Teachers />
      <News />
    </>
  );
}
