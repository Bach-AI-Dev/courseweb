import { hueFromId } from "../utils/format";

export default function CourseCover({ course }) {
  return (
    <div className="cover" style={{ "--hue": hueFromId(course.id) }}>
      {course.thumbnailUrl ? <img src={course.thumbnailUrl} alt="" /> : <span>{course.title}</span>}
    </div>
  );
}
