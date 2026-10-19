package com.baitapnhom.courseweb.exception;

/**
 * Enum chứa tất cả các error code của ứng dụng
 * 
 * Quy tắc:
 * - Code 1000-1099: User & Authentication
 * - Code 1100-1199: Course
 * - Code 1200-1299: Enrollment
 * - Code 9999: Uncategorized
 */
public enum ErrorCode {
    // ============== UNCATEGORIZED ==============
    UNCATEGORIED_EXISTED(9999, "Lỗi chưa phân loại"),

    // ============== USER & AUTHENTICATION (1000-1099) ==============
    USER_EXISTED(1001, "User đã tồn tại"),
    EMAIL_EXISTED(1002, "Email đã tồn tại"),

    USERNAME_INVALID(1003, "Username phải có tối thiểu 3 ký tự và tối đa 50 ký tự"),
    USERNAME_EMPTY(1004, "Username không được để trống"),

    PASSWORD_INVALID(1005, "Password phải có tối thiểu 6 ký tự"),
    PASSWORD_EMPTY(1006, "Password không được để trống"),

    EMAIL_INVALID(1007, "Email không đúng định dạng"),
    EMAIL_EMPTY(1008, "Email không được để trống"),

    USER_NOT_EXISTED(1009, "User không tồn tại"),
    UNAUTHENTICATED(1010, "Tài khoản hoặc mật khẩu không chính xác"),

    UNAUTHORIZED(1011, "Bạn không có quyền truy cập tài nguyên này"),
    UNAUTHORIZED_ROLE_CREATION(1012, "Bạn không thể tạo tài khoản này"),

    // ============== VIDEO & CONTENT (1020-1099) ==============
    VIDEO_NOT_FOUND(1021, "Không tìm thấy thông tin video bài học"),

    // ============== COURSE (1100-1199) ==============
    COURSE_NOT_FOUND(1100, "Khóa học không tồn tại"),
    COURSE_NOT_ACTIVE(1101, "Khóa học hiện không mở đăng ký"),
    INVALID_COURSE_PRICE(1102, "Giá khóa học không hợp lệ"),
    COURSE_NOT_APPROVED(1103, "Khóa học chưa được phê duyệt"),
    INVALID_COURSE_ID(1104, "Course ID không được để trống"),
    CATEGORY_NOT_FOUND(1105, "Danh mục khóa học không tồn tại"),
    TEACHER_NOT_FOUND(1106, "Giáo viên không tồn tại"),
    CANNOT_DELETE_COURSE_HAS_LESSONS(1107, "Không thể xóa khóa học vì đã có bài học liên kết"),
    CANNOT_DELETE_COURSE_HAS_ENROLLMENTS(1108, "Không thể xóa khóa học vì đã có học viên đăng ký"),

    // ============== ENROLLMENT (1200-1299) ==============
    ENROLLMENT_NOT_FOUND(1200, "Enrollment không tồn tại"),
    ALREADY_ENROLLED(1201, "Bạn đã đăng ký khóa học này rồi"),
    COURSE_COMPLETED(1202, "Bạn đã hoàn thành khóa học này, không thể đăng ký lại"),
    CANNOT_CANCEL_COMPLETED(1203, "Không thể hủy khóa học đã hoàn thành"),
    INVALID_ENROLLMENT_ID(1204, "Enrollment ID không hợp lệ"),
    INVALID_STUDENT_ID(1205, "Student ID không được để trống"),
    INVALID_PAGE_SIZE(1206, "Kích thước trang không hợp lệ"),
    CANNOT_ENROLL_OWN_COURSE(1207, "Giảng viên không thể tự đăng ký khóa học của chính mình"),
    ADMIN_CANNOT_ENROLL(1208, "Tài khoản Quản trị viên không được phép đăng ký học như học viên"),
    MAX_ACTIVE_COURSES_EXCEEDED(1209, "Học viên chỉ được học tối đa 1 khóa học ACTIVE cùng lúc"),
    COURSE_CAPACITY_EXCEEDED(1210, "Khóa học đã đạt sĩ số tối đa 30 học viên ACTIVE"),
    COURSE_NOT_COMPLETED_YET(1211, "Học viên chưa hoàn thành toàn bộ bài học trong khóa học"),
    CANCEL_DEADLINE_EXPIRED(1212, "Đã quá thời hạn 7 ngày kể từ ngày đăng ký, không thể hủy khóa học"),
    CANNOT_CANCEL_STARTED_COURSE(1213, "Không thể hủy khóa học khi đã hoàn thành ít nhất một bài học"),
    COURSE_ALREADY_ARCHIVED(1214, "Khóa học đã bị lưu trữ (ARCHIVED), không thể thao tác"),
    PAGE_OUT_OF_BOUNDS(1215, "Số trang yêu cầu vượt quá tổng số trang hiện có"),
    RE_ENROLL_COOLDOWN_ACTIVE(1216, "Bạn vừa hủy khóa học này, vui lòng đợi đủ 24 giờ để đăng ký lại"),
    PAID_COURSE_LIMIT_EXCEEDED(1217, "Mỗi học viên chỉ được phép có tối đa 2 khóa học có phí ở trạng thái ACTIVE"),
    PAID_COURSE_REQUIRES_PAYMENT(1218, "Khóa học có phí yêu cầu thanh toán trước khi kích hoạt"),
    INVALID_COURSE_TRANSFER(1219, "Khóa học chuyển đổi không hợp lệ hoặc trùng nhau"),
    TRANSFER_PRICE_EXCEEDED(1220, "Khóa học chuyển đến có mức giá cao hơn khóa học hiện tại"),
    INSUFFICIENT_LEARNING_PROGRESS(1221, "Chưa đạt đủ 100% số bài học và tối thiểu 80% tổng thời lượng video để tốt nghiệp"),
    INVALID_DATE_RANGE(1222, "Ngày bắt đầu (fromDate) không được lớn hơn ngày kết thúc (toDate)"),
    INVALID_INACTIVE_DAYS(1223, "Số ngày không hoạt động (maxInactiveDays) phải lớn hơn 0"),
    USER_NOT_FOUND(4044, "Không tìm thấy hồ sơ học viên tương ứng với tài khoản này");

    private final int code;
    private final String message;

    /**
     * Constructor của ErrorCode enum
     * 
     * @param code    Mã lỗi số (dùng để gửi cho client)
     * @param message Thông báo lỗi (tiếng Việt)
     */
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
