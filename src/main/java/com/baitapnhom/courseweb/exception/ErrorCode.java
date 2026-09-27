package com.baitapnhom.courseweb.exception;

public enum ErrorCode {

    UNCATEGORIED_EXISTED(9999, "Lỗi chưa phần loại(uncategoried)"),
    USER_EXISTED(1001, "User đã tồn tại"),
    EMAIL_EXISTED(1002, "Email đã tồn tại"),

    USERNAME_INVALID(1003, "Username phải có tối thiểu 3 ký tự và tối đa 50 ký tự.............."),
    USERNAME_EMPTY(1004, "Username không được để trống!!!!!!!!!!!!!!!!!!!!"),

    PASSWORD_INVALID(1005, "Password phải có tối thiểu 6 ký tự............"),
    PASSWORD_EMPTY(1006, "Password không được để trống!!!!!!!!!!!!!!!!!!!!"),

    EMAIL_INVALID(1007, "Email không đúng định dạng..........."),
    EMAIL_EMPTY(1008, "Email không được để trống!!!!!!!!!!!!!!!!!!!!"),
            
    USER_NOT_EXISTED(1009, "User không tồn tại"),
    UNAUTHENTICATED(1010, "Mật khẩu không chính xác hoặc chưa được xác thực"),

    VIDEO_NOT_FOUND(1011, "Không tìm thấy thông tin video bài học!"),
    ENROLLMENT_NOT_FOUND(1012, "Học viên chưa đăng ký khóa học này, không thể cập nhật tiến độ!"),
    
    COURSE_NOT_FOUND(1020, "Khóa học không tồn tại"),
    ALREADY_ENROLLED(1013, "Bạn đã đăng ký khóa học này rồi!"),
    COURSE_COMPLETED(1014, "Bạn đã hoàn thành khóa học này, không thể đăng ký lại!");
    ;

    
    
    private ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
