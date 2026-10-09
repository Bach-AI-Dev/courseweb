package com.baitapnhom.courseweb.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.baitapnhom.courseweb.entity.User;
import com.baitapnhom.courseweb.enums.Role;
import com.baitapnhom.courseweb.repository.UserRepository;

@Configuration
public class ApplicationInitConfig {
    // Khởi chạy khi bắt đầu, thêm User admin vào nếu database chưa có User vs quyền ADMIN
    @Autowired
    PasswordEncoder passwordEncoder;
    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            // Đổi logic: Kiểm tra xem trong DB đã có BẤT KỲ tài khoản nào mang quyền ADMIN chưa
            if (!userRepository.existsByRole(Role.ADMIN)) {
                User user = new User();
                user.setUsername("admin");

                // Bắt buộc phải có email vì Entity quy định nullable = false
                user.setEmail("admin@gmail.com");

                // Mã hóa mật khẩu
                user.setPassword(passwordEncoder.encode("admin_kieu_thanh_dat"));

                // Gán quyền 
                user.setRole(Role.ADMIN);

                // Lưu xuống database
                userRepository.save(user);

                System.out.println("Tai khoan ADMIN da duoc tao tu dong");
            }
        };
    }
}
