package com.baitapnhom.courseweb.entity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.*;
import com.baitapnhom.courseweb.enums.*;

//Đánh dấu cho Spring Data JPA và Hibernate biết class này là một thực thể dữ liệu cần được quản lý
@Entity
@Table(name = "users")
public class User {

    @Id
    // Kiểu id là những chuỗi được Random ngẫu nhiên không trùng lặp
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "full_name")
    private String fullName;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(length = 10)
    private String phone;

    // nếu không gắn @Enumerated, Hibernate sẽ map enum thành số nguyên ORDINAL (0,
    // 1, 2). Trong khi đó, cột role trong cơ sở dữ liệu MySQL lại lưu kiểu chuỗi
    // (VARCHAR hoặc ENUM('STUDENT', 'TEACHER', 'ADMIN')). Điều này sẽ gây lỗi xung
    // đột kiểu dữ liệu khi chạy.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.STUDENT;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        this.updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        // if (this.role == null) {
        //     this.role = Role.STUDENT;
        // }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public User() {
    }

    public User(String id, String fullName, String email, String password, String phone, Role role, String username) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role != null ? role : Role.STUDENT;
        this.username = username;
    }

    // Phần getter và setter

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
