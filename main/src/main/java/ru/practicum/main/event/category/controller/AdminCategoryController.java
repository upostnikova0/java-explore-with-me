package ru.practicum.main.event.category.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.category.dto.CategoryDto;
import ru.practicum.main.event.category.dto.NewCategoryDto;
import ru.practicum.main.event.category.service.CategoryService;

import javax.validation.Valid;

@Slf4j
@AllArgsConstructor
@Validated
@RestController
@RequestMapping("/admin/categories")
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("POST category by admin");
        return categoryService.createCategory(newCategoryDto);
    }

    @PatchMapping("/{categoryId}")
    public CategoryDto updateCategory(@PathVariable Long categoryId, @RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("PATCH category by admin");
        return categoryService.updateCategory(categoryId, newCategoryDto);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long categoryId) {
        log.info("DELETE category by admin");
        categoryService.deleteCategoryById(categoryId);
    }
}
