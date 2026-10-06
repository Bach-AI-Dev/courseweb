package com.baitapnhom.courseweb.service;

import com.baitapnhom.courseweb.dto.request.CategoryRequest;
import com.baitapnhom.courseweb.dto.response.CategoryResponse;
import com.baitapnhom.courseweb.entity.Category;
import com.baitapnhom.courseweb.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // Helper map từ Entity sang Response DTO (Không dùng Lombok / MapStruct)
    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    // 1. Tạo Category mới
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("Tên danh mục đã tồn tại!");
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category savedCategory = categoryRepository.save(category);
        return toResponse(savedCategory);
    }

    // 2. Lấy toàn bộ danh mục
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryResponse> responses = new ArrayList<>();
        for (Category category : categories) {
            responses.add(toResponse(category));
        }
        return responses;
    }

    // 3. Lấy theo ID
    public CategoryResponse getCategoryById(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));
        return toResponse(category);
    }
    
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    // 4. Cập nhật Category
    public CategoryResponse updateCategory(String id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));

        if (categoryRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new RuntimeException("Tên danh mục đã được sử dụng bởi danh mục khác!");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category updatedCategory = categoryRepository.save(category);
        return toResponse(updatedCategory);
    }
    
    @PreAuthorize("has('ADMIN')")
    // 5. Xóa Category (Kiểm tra quan hệ với Course)
    public void deleteCategory(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));

        // Kiểm tra nếu danh mục đã có khóa học bên trong
        if (category.getCourses() != null && !category.getCourses().isEmpty()) {
            throw new RuntimeException("Không thể xóa danh mục này vì đang có "
                    + category.getCourses().size() + " khóa học liên kết!");
        }

        categoryRepository.delete(category);
    }
}
