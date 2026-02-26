package com.example.aslapp_backend.repositories;

import com.example.aslapp_backend.models.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {


    Optional<Category> findById(Long aLong);

    Optional<Category> findByName(String name);

    Boolean existsByName(String name);


    Page<Category> findAll(Pageable pageable);
}
