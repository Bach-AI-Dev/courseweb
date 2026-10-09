// ADMIN và TEACHER là "nhân sự": quản lý khóa học, bài học và không cần đăng ký học.
export const isStaff = (user) => user?.role === "ADMIN" || user?.role === "TEACHER";
