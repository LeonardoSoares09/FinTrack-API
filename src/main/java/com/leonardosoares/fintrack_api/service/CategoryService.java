package com.leonardosoares.fintrack_api.service;

import com.leonardosoares.fintrack_api.config.exception.ResourceNotFoundException;
import com.leonardosoares.fintrack_api.model.Category;
import com.leonardosoares.fintrack_api.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category createCategory(Category category) {
        //LOGICA DO DTO QUE IREI IMPLEMENTAR DEPOIS, TRANSFORMANDO ELE EM ENTIDADE SALVANDO NO BANCO E RETORNANDO COMO DTO

        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        //IMPLEMENTAR DEPOIS PARA RETORNAR DTO, UTILIZANDO STREAM E MAP PARA APLICAR O MAPPER
        return categoryRepository.findAll();
    }

    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found!"));
    }

    public Category updateCategory(UUID id, Category category) {
        Category c = getCategoryById(id);
        c.setName(category.getName());
        c.setDescription(category.getDescription());

        return categoryRepository.save(c);
    }

    public void deleteCategory(UUID id) {
        categoryRepository.deleteById(id);
    }
}
