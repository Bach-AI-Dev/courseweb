package com.baitapnhom.courseweb.entity;

import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "course_categories") // Ép Spring Boot tìm đúng bảng này thay vì bảng "categories"
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // Bỏ unique = true vì trong DB SQL không cài đặt ràng buộc này cho name
    @Column(nullable = false, length = 100)
    private String name;

    // BỔ SUNG: Trường description có trong CSDL nhưng code cũ của bạn bị thiếu
    @Column(columnDefinition = "TEXT")
    private String description;

    // SỬA TÊN: Đổi 'coursers' thành 'courses' cho chuẩn tiếng Anh
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Course> courses;

    public Category() {
    }

    // ================= GETTER VÀ SETTER =================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
}
