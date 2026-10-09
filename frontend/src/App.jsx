import { Routes, Route } from "react-router-dom";
import Layout from "./components/Layout";
import HomePage from "./pages/HomePage";
import CoursesPage from "./pages/CoursesPage";
import CourseDetailPage from "./pages/CourseDetailPage";
import LessonPage from "./pages/LessonPage";
import MyCoursesPage from "./pages/MyCoursesPage";
import NotFoundPage from "./pages/NotFoundPage";

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<HomePage />} />
        <Route path="khoa-hoc" element={<CoursesPage />} />
        <Route path="khoa-hoc/:courseId" element={<CourseDetailPage />} />
        <Route path="khoa-hoc/:courseId/bai-hoc/:lessonId" element={<LessonPage />} />
        <Route path="khoa-hoc-cua-toi" element={<MyCoursesPage />} />
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  );
}
