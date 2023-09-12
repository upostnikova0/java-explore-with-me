package ru.practicum.main.event.category.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.main.event.category.dto.CategoryDto;
import ru.practicum.main.event.category.dto.NewCategoryDto;
import ru.practicum.main.event.category.mapper.CategoryMapper;
import ru.practicum.main.event.category.model.Category;
import ru.practicum.main.event.category.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        if (existsByName(newCategoryDto.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "name is already exists");
        }
        Category category = categoryRepository.save(categoryMapper.toEntity(newCategoryDto));
        log.info("new category {} was added to db", category);
        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long categoryId, NewCategoryDto newCategoryDto) {
        Category category = findById(categoryId);
        category.setName(newCategoryDto.getName());
        categoryRepository.save(category);
        log.info("category with id {} was updated successfully", categoryId);
        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public void deleteCategoryById(Long categoryId) {
        findById(categoryId);
        log.info("category with id {} was successfully deleted", categoryId);
        categoryRepository.deleteById(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long categoryId) {
        Category category = findById(categoryId);
        log.info("found category: {}", category);
        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        Page<Category> foundCategories = categoryRepository.findAll(PageRequest.of(from / size, size));
        log.info("found categories: {}", foundCategories);
        return foundCategories
                .stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Category findById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                String.format("category with id %d was not found", categoryId)
                        )
                );
        log.info("found category: {}", category);
        return category;
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsByName(String categoryName) {
        return categoryRepository.existsByName(categoryName);
    }
}
