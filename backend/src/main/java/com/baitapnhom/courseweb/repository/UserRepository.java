package com.baitapnhom.courseweb.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.baitapnhom.courseweb.entity.User;
import com.baitapnhom.courseweb.enums.Role;

// Kế thừa siêu nạp extends JpaRepository<User, Long>
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // existsByUsername và existsByEmail trả về true nếu tìm thấy ngược lại trả về
    // false
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    // Lấy toàn bộ thông tin tài khoản dựa vào tên đăng nhập hoặc email.
    // bọc trong kiểu Optional<User> tránh lỗi NullPointerException
    Optional<User> findByUsername(String username);

    // Optional<User> findByEmail(String email);

    boolean existsByRole(Role role);
}
