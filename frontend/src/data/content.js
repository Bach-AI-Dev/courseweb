export const NAV = [
  { label: "Giới thiệu", href: "/#gioi-thieu" },
  { label: "Khóa học", href: "/khoa-hoc" },
  { label: "Giảng viên", href: "/#giang-vien" },
  { label: "Tin tức", href: "/#tin-tuc" },
  { label: "Liên hệ", href: "/#lien-he" },
];

export const STATS = [
  { n: "10+", l: "năm kinh nghiệm" },
  { n: "5.000", l: "học viên đã theo học" },
  { n: "40", l: "giảng viên" },
  { n: "98%", l: "phụ huynh hài lòng" },
];

export const ABOUT = [
  { title: "Lớp học nhỏ", desc: "Mỗi lớp tối đa 12 học viên để giáo viên theo sát từng em." },
  { title: "Học để hiểu", desc: "Tập trung vào bản chất kiến thức thay vì học thuộc." },
  { title: "Theo dõi tiến độ", desc: "Phụ huynh nhận báo cáo học tập định kỳ sau mỗi chặng." },
];

export const TEACHERS = [
  { name: "Nguyễn Minh Anh", role: "Giảng viên Toán tư duy" },
  { name: "Trần Quốc Bảo", role: "Giảng viên Logic" },
  { name: "Lê Thu Hà", role: "Giảng viên Ngữ văn" },
];

export const NEWS = [
  { date: "05/10/2026", title: "Khai giảng các lớp tư duy học kỳ mới" },
  { date: "28/09/2026", title: "Phương pháp học chủ động: bắt đầu từ đâu?" },
  { date: "15/09/2026", title: "Học bổng dành cho học viên xuất sắc" },
];

export const ROLES = [
  { value: "STUDENT", label: "Học sinh/Sinh viên", desc: "Tham gia học các khóa học" },
  // Backend chỉ cho đăng ký STUDENT. Tài khoản giáo viên do quản trị viên cấp.
  { value: "TEACHER", label: "Giáo viên", desc: "Liên hệ quản trị viên để được cấp tài khoản", disabled: true },
];

export const ROLE_LABEL = { STUDENT: "Học sinh/Sinh viên", TEACHER: "Giáo viên", ADMIN: "Quản trị viên" };
