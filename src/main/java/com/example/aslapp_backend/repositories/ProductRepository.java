package com.example.aslapp_backend.repositories;

import com.example.aslapp_backend.models.Category;
import com.example.aslapp_backend.models.Product;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {


    Optional<Product> findById(Long aLong);


    // Basic pagination
    Page<Product> findAll(Pageable pageable);



    Optional<Product> findByName(String name);

    // Search by CategoryDTO name (Product.CategoryDTO is an entity)
    Page<Product> findByCategory(Category category, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Product> searchProductBy(@Param("search") String search, Pageable pageable);
}