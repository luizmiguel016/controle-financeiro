
package com.ifpr.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ifpr.backend.dto.CategoryCreationDTO;
import com.ifpr.backend.dto.CategoryResponseDTO;
import com.ifpr.backend.entity.Category;
import com.ifpr.backend.entity.User;
import com.ifpr.backend.repository.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public CategoryResponseDTO create(CategoryCreationDTO dto, User owner) {

        Category category = new Category();

        category.setName(dto.name().trim());
        category.setType(dto.type());
        category.setUser(owner);

        Category savedCategory = categoryRepository.save(category);

        return new CategoryResponseDTO(
                savedCategory.getId(),
                savedCategory.getName(),
                savedCategory.getType(),
                owner.getId()
        );
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> listOwned(User owner) {

        List<Category> categories = categoryRepository.findByUser_Id(owner.getId());

        return categories.stream()
                .map(category -> new CategoryResponseDTO(
                        category.getId(),
                        category.getName(),
                        category.getType(),
                        owner.getId()
                ))
                .toList();
    }
}
