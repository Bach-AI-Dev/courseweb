package com.baitapnhom.courseweb.repository;

import com.baitapnhom.courseweb.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    // Kiểm tra trùng tên danh mục
    boolean existsByName(String name);

    // Kiểm tra trùng tên khi update (trừ chính nó)
    boolean existsByNameAndIdNot(String name, String id);
}