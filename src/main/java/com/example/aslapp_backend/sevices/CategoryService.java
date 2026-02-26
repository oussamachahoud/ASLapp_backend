package com.example.aslapp_backend.sevices;

import com.example.aslapp_backend.DTOs.responseDTOs.CategoryDTO;
import com.example.aslapp_backend.Exeption.BusinessException;
import com.example.aslapp_backend.models.Category;
import com.example.aslapp_backend.repositories.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CategoryService {

    final private CategoryRepository categoryRepository;


    public CategoryDTO create(String name){
        if (categoryRepository.existsByName(name)) throw new BusinessException(HttpStatus.CONFLICT,"Category already exist");
        Category c =new Category();
        c.setName(name);
        return tocategoryDTO(categoryRepository.save(c));
    }




    public void delete(Long id ){
        Category c = categoryRepository.findById(id).orElseThrow(() ->new BusinessException(HttpStatus.CONFLICT,"Category not exist"));
        categoryRepository.delete(c);
    }

    public CategoryDTO Patch(Long id, String name) {
        if (categoryRepository.existsByName(name)) throw new BusinessException(HttpStatus.CONFLICT,"Category already exist");
        Category c =new Category();
        c.setId(id);
        c.setName(name);
        return tocategoryDTO(categoryRepository.save(c));
    }

    public Page<CategoryDTO> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(i ->tocategoryDTO(i));
    }


    private CategoryDTO tocategoryDTO(Category c){
        return CategoryDTO.builder()
                .id(c.getId())
                .name(c.getName())
                .build();
    }
}
