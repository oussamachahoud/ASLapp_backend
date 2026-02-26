package com.example.aslapp_backend.repositories;

import com.example.aslapp_backend.models.Category;
import com.example.aslapp_backend.models.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    // TestEntityManager is a helper to manually set up data in the DB without going through the repo
    @Autowired
    private TestEntityManager entityManager;

    // Clean the DB after every test to ensure isolation
    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    void whenFindByName_thenReturnProduct() {
        // given
        Category category = new Category("Vehicles");
        entityManager.persist(category); // Save CategoryDTO first (FK constraint)

        Product product = new Product("car", 11111, "dizel", 3, category);
        entityManager.persist(product);
        entityManager.flush(); // Force SQL execution

        // when
        Optional<Product> found = productRepository.findByName("car"); // Assuming you have this method

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("car");
        assertThat(found.get().getPrice()).isEqualTo(11111);
    }

    @Test
    void whenSave_thenIdIsGenerated() {
        // given
        Category category = new Category("Electronics");
        entityManager.persist(category);
        Product product = new Product("Laptop", 999, "i7", 5, category);

        // when
        Product saved = productRepository.save(product);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getId()).isGreaterThan(0);
    }

    @Test
    void whenDeleteById_thenProductIsRemoved() {
        // given
        Category category = new Category("Food");
        entityManager.persist(category);
        Product product = new Product("Apple", 5, "Red", 10, category);
        productRepository.save(product);

        // when
        productRepository.deleteById(product.getId());

        // then
        Optional<Product> deleted = productRepository.findById(product.getId());
        assertThat(deleted).isEmpty();
    }

    @Test
    void whenFindAll_thenReturnList() {
        // given
        Category category = new Category("Test");
        entityManager.persist(category);

        Product p1 = new Product("A", 1, "A", 1, category);
        Product p2 = new Product("B", 2, "B", 2, category);
        entityManager.persist(p1);
        entityManager.persist(p2);
        entityManager.flush();

        // when
        List<Product> products = productRepository.findAll();

        // then
        assertThat(products).hasSize(2);
        assertThat(products).extracting(Product::getName).containsExactlyInAnyOrder("A", "B");
    }
}
