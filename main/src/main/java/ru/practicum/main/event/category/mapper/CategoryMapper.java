package ru.practicum.main.event.category.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.main.event.category.dto.CategoryDto;
import ru.practicum.main.event.category.dto.NewCategoryDto;
import ru.practicum.main.event.category.model.Category;

@Component
public class CategoryMapper {
    public Category toEntity(NewCategoryDto newCategoryDto) {
        return new Category(
                null,
                newCategoryDto.getName()
        );
    }

    public CategoryDto toDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }
}
