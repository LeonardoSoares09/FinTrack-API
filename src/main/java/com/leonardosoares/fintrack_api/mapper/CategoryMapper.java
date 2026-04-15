package com.leonardosoares.fintrack_api.mapper;

import org.springframework.stereotype.Component;

import com.leonardosoares.fintrack_api.controller.dto.CategoryResponse;
import com.leonardosoares.fintrack_api.model.Category;

@Component
public class CategoryMapper {
     public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
            category.getId(), 
            category.getName(), 
            category.getDescription());
    }
    
}
