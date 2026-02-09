package com.example.aslapp_backend.sevices;

import com.example.aslapp_backend.models.Category;
import com.example.aslapp_backend.models.Product;
import com.example.aslapp_backend.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
@Slf4j
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private  ProductRepository productRepository;
    @Mock
    private  StorageService storageService;
    @InjectMocks
    private ProductService productService;

    @Test
    void Test_getAllProducts(){
        Product p= new Product("car",11111,"dizel",3,new Category());

      Pageable pageable = PageRequest.of(0,1);
        Page<Product> page=  new PageImpl<Product>(List.of(p), pageable, List.of(p).size());
        when( productRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(p), pageable, 1L));

        Page<Product> result = productService.getAllProducts(0,1,"id","ASC");

        assertThat(result).isEqualTo(page);
        assertNotNull(result);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("car");
        verify(productRepository).findAll(any(Pageable.class));
        //NotAMock
        //Argument passed to verify() is of type ProductService and is not a mock
       // verify(productService).createSort(anyString(),anyString());
    }
    @Test
    void Test_createSort(){
        Sort sort =productService.createSort("id","asc");
        Sort sort1 =Sort.by("id").ascending();
        Sort sort2 =Sort.by("name").ascending();
        assertNotNull(sort);
        log.info("*******************************************************");
        log.info(sort.toString());
        assertThat(sort.toString()).isEqualTo(sort1.toString());
        assertThat(sort).isNotEqualTo(sort2);

    }

    @Test
    void Test_searchProducts(){
        Product p1= new Product("car",1111,"dizel",3,new Category());
        Product p2= new Product("car2",11111,"dizel",3,new Category());
        Product p3= new Product("xxxx",11111,"dizel",3,new Category());


        Pageable pageable = PageRequest.of(0,1);
        Page<Product> page=  new PageImpl<Product>(List.of(p1,p2,p3), pageable, 3);
        when( productRepository.searchProductBy(anyString(),any(Pageable.class))).thenReturn(page);

        Page<Product> result = productService.searchProducts("xxxx",0,3,"id","asc");

        assertNotNull(result);
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent())
                .extracting(Product::getName)
                .containsExactly("car", "car2", "xxxx");
        verify(productRepository).searchProductBy(anyString(),any(Pageable.class));

    }
}
