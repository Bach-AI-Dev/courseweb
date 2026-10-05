package com.baitapnhom.courseweb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baitapnhom.courseweb.dto.request.RegisterRequest;
import com.baitapnhom.courseweb.dto.request.UserUpdateRequest;
import com.baitapnhom.courseweb.dto.response.UserResponse;
import com.baitapnhom.courseweb.entity.Student;
import com.baitapnhom.courseweb.entity.User;
import com.baitapnhom.courseweb.enums.Role;
import com.baitapnhom.courseweb.exception.AppException;
import com.baitapnhom.courseweb.exception.ErrorCode;
import com.baitapnhom.courseweb.repository.StudentRepository;
import com.baitapnhom.courseweb.repository.UserRepository;

import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User register(RegisterRequest request) {

        User user = new User();

        // Kiểm tra trùng lặp
        if (userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

        if (userRepository.existsByEmail(request.getEmail()))
            throw new AppException(ErrorCode.EMAIL_EXISTED);

        // Mã hóa password qua Bcrypy là implementation của PasswordEncoder
        // PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());

        // CHẶN TẠO TÀI KHOẢN ADMIN TỪ API ĐĂNG KÝ
        if (request.getRole() == Role.ADMIN || request.getRole() == Role.TEACHER) {
            // Bạn có thể tạo thêm ErrorCode.INVALID_ROLE trong file ErrorCode.java để dùng
            throw new AppException(ErrorCode.UNAUTHORIZED_ROLE_CREATION);

        }

        Role role = Role.STUDENT;
        user.setRole(role);
        user = userRepository.save(user);

        // Khi đăng ký User, tự động tạo luôn bản ghi trong bảng students tương ứng với
        // ID của User
        if (role == Role.STUDENT) {
            Student student = new Student(user);
            studentRepository.save(student);
        }

        return user;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUser(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    public User updateUser(String userId, UserUpdateRequest request) {
        User user = getUser(userId);
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(String id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
        }
        userRepository.deleteById(id);
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());

        if (user.getRole() != null) {
            response.setRole(user.getRole().name());
        }

        return response;
    }

}
