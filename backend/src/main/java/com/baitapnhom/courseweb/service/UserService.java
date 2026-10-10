package com.baitapnhom.courseweb.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baitapnhom.courseweb.dto.request.RegisterRequest;
import com.baitapnhom.courseweb.dto.request.UserUpdateRequest;
import com.baitapnhom.courseweb.dto.response.UserResponse;
import com.baitapnhom.courseweb.entity.User;
import com.baitapnhom.courseweb.enums.Role;
import com.baitapnhom.courseweb.exception.AppException;
import com.baitapnhom.courseweb.exception.ErrorCode;
import com.baitapnhom.courseweb.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Đăng kí User
    @Transactional
    public UserResponse register(RegisterRequest request) {
        // Chặn tạo tài khoản ADMIN hoặc TEACHER
        if (request.getRole() == Role.ADMIN || request.getRole() == Role.TEACHER) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ROLE_CREATION);
        }

        User user = buildBaseUser(request);
        user.setRole(Role.STUDENT);
        user = userRepository.save(user);

        return mapToUserResponse(user);
    }

    // API tạo giảng viên(chỉ ADMIN)
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse registerteacher(RegisterRequest request) {
        // Chặn tạo tài khoản ADMIN từ API này
        if (request.getRole() == Role.ADMIN) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ROLE_CREATION);
        }

        User user = buildBaseUser(request);
        user.setRole(Role.TEACHER);
        user = userRepository.save(user);

        return mapToUserResponse(user);
    }

    // ADMIN xem danh sách User
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    // ADMIN xem từng User theo id
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUser(String id) {
        User user = getUserEntity(id);
        return mapToUserResponse(user);
    }

    // API cập nhật thông tin User
    @Transactional
    public UserResponse updateMyInfo(UserUpdateRequest request) {
        // Tìm theo username
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();
        // Xử lý trùng lặp
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        // Cập nhật các thông tin mới
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());

        user = userRepository.save(user);
        return mapToUserResponse(user);
    }

    // ADMIN xóa tài khoản User
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    // User lấy thông tin cá nhân
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return mapToUserResponse(user);
    }

    // Hàm gom chung logic kiểm tra và tạo khung User để tái sử dụng
    private User buildBaseUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

        if (userRepository.existsByEmail(request.getEmail()))
            throw new AppException(ErrorCode.EMAIL_EXISTED);

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return user;
    }

    // Hàm tìm User theo ID
    private User getUserEntity(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    // Hàm chuẩn hóa dữ liệu từ Entity (User) sang DTO (UserResponse)
    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        if (user.getRole() != null) {
            response.setRole(user.getRole().name());
        }
        return response;
    }
}
