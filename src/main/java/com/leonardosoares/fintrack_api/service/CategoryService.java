package com.leonardosoares.fintrack_api.service;

import com.leonardosoares.fintrack_api.config.exception.ResourceNotFoundException;
import com.leonardosoares.fintrack_api.controller.dto.CategoryRequest;
import com.leonardosoares.fintrack_api.controller.dto.CategoryResponse;
import com.leonardosoares.fintrack_api.mapper.CategoryMapper;
import com.leonardosoares.fintrack_api.model.Category;
import com.leonardosoares.fintrack_api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        Category category = new Category();
        category.setName(categoryRequest.name());
        category.setDescription(categoryRequest.description());
        categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }

    public List<CategoryResponse> getAllCategories() {
        
        return categoryRepository.findAll()
        .stream()
        .map(category -> categoryMapper.toResponse(category))
        .toList();
    }

    public CategoryResponse getCategoryById(UUID id) {

        Category c = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found!"));
        return categoryMapper.toResponse(c);
    }

    public CategoryResponse updateCategory(UUID id, CategoryRequest categoryRequest) {
        Category c = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found!"));
        c.setName(categoryRequest.name());
        c.setDescription(categoryRequest.description());
        categoryRepository.save(c);
        return categoryMapper.toResponse(c);
    }

    public void deleteCategory(UUID id) {
        categoryRepository.deleteById(id);
    }
}
