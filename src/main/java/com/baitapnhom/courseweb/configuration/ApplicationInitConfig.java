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
    // Khởi chạy khi App start, thêm User admin vào 

    @Autowired 
    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository){
        return args -> {
           // Kiểm tra xem database đã có tài khoản admin chưa
            if (userRepository.findByUsername("admin").isEmpty()) {
                
                // Sử dụng Java thuần (new Object và setter) thay vì Lombok Builder
                User user = new User();
                user.setUsername("admin");
                
                // Bắt buộc phải có email vì Entity quy định nullable = false
                user.setEmail("admin@gmail.com");
                
                // Mã hóa mật khẩu trước khi lưu (ví dụ mật khẩu là "admin")
                user.setPassword(passwordEncoder.encode("admin_kieu_thanh_dat"));
                
                // Gán quyền trực tiếp bằng Enum, không dùng HashSet
                user.setRole(Role.ADMIN);

                // Lưu xuống database (trong video bị thiếu bước save này)
                userRepository.save(user);
                
                System.out.println("Tai khoan ADMIN da duoc tao tu dong!!!!!!!!!!!!!!!!!!");
            }
        };
    }
}
