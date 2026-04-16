package com.leonardosoares.fintrack_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.leonardosoares.fintrack_api.config.exception.ResourceNotFoundException;
import com.leonardosoares.fintrack_api.controller.dto.CategoryRequest;
import com.leonardosoares.fintrack_api.controller.dto.CategoryResponse;
import com.leonardosoares.fintrack_api.mapper.CategoryMapper;
import com.leonardosoares.fintrack_api.model.Category;
import com.leonardosoares.fintrack_api.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    @DisplayName("Deve criar uma categoria com sucesso")
    void createCategory_ShouldSaveAndReturnResponse() {
        CategoryRequest request = new CategoryRequest("Lazer", "Gastos com diversão");
        Category category = new Category(); 
        CategoryResponse expectedResponse = new CategoryResponse(UUID.randomUUID(), "Lazer", "Gastos com diversão");

        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toResponse(any(Category.class))).thenReturn(expectedResponse);

        CategoryResponse response = categoryService.createCategory(request);

        assertNotNull(response);
        assertEquals("Lazer", response.name());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Deve retornar lista de categorias mapeada")
    void getAllCategories_ShouldReturnMappedList() {
        Category cat1 = new Category();
        Category cat2 = new Category();
        List<Category> categories = List.of(cat1, cat2);
        
        CategoryResponse res1 = new CategoryResponse(UUID.randomUUID(), "Cat1", "Desc1");
        CategoryResponse res2 = new CategoryResponse(UUID.randomUUID(), "Cat2", "Desc2");

        when(categoryRepository.findAll()).thenReturn(categories);
        when(categoryMapper.toResponse(cat1)).thenReturn(res1);
        when(categoryMapper.toResponse(cat2)).thenReturn(res2);

        List<CategoryResponse> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        verify(categoryRepository, times(1)).findAll();
        verify(categoryMapper, times(2)).toResponse(any());
    }

    @Test
    @DisplayName("Deve atualizar categoria quando ela existir")
    void updateCategory_WhenExists_ShouldUpdateAndReturnResponse() {
        UUID id = UUID.randomUUID();
        CategoryRequest request = new CategoryRequest("Novo Nome", "Nova Descrição");
        Category existingCategory = new Category();
        CategoryResponse expectedResponse = new CategoryResponse(id, "Novo Nome", "Nova Descrição");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);
        when(categoryMapper.toResponse(existingCategory)).thenReturn(expectedResponse);

        CategoryResponse response = categoryService.updateCategory(id, request);

        assertEquals("Novo Nome", response.name());
        verify(categoryRepository).save(existingCategory);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar categoria inexistente")
    void updateCategory_WhenDoesNotExist_ShouldThrowException() {
        UUID id = UUID.randomUUID();
        CategoryRequest request = new CategoryRequest("Nome", "Desc");
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(id, request));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve chamar deleteById no repositório")
    void deleteCategory_ShouldCallRepositoryDelete() {
        UUID id = UUID.randomUUID();

        categoryService.deleteCategory(id);

        verify(categoryRepository, times(1)).deleteById(id);
    }
}