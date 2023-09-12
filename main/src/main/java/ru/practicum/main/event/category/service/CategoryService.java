package ru.practicum.main.event.category.service;

import ru.practicum.main.event.category.dto.CategoryDto;
import ru.practicum.main.event.category.dto.NewCategoryDto;
import ru.practicum.main.event.category.model.Category;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    void deleteCategoryById(Long categoryId);

    CategoryDto updateCategory(Long categoryId, NewCategoryDto newCategoryDto);

    CategoryDto getCategoryById(Long categoryId);

    List<CategoryDto> getAllCategories(Integer from, Integer size);

    Category findById(Long categoryId);

    Boolean existsByName(String categoryName);
}
