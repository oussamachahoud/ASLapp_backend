package com.example.aslapp_backend.sevices;


import com.example.aslapp_backend.DTOs.responseDTOs.ProductResponseDTO;
import com.example.aslapp_backend.DTOs.responseDTOs.CategoryDTO;
import com.example.aslapp_backend.DTOs.requestDTOs.ProduitDto;
import com.example.aslapp_backend.Exeption.BusinessException;
import com.example.aslapp_backend.models.Category;
import com.example.aslapp_backend.models.Product;
import com.example.aslapp_backend.repositories.CategoryRepository;
import com.example.aslapp_backend.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryReposotory;
    private final StorageService storageService;

    //adding methode
    public Sort createSort(String sortBy, String direction){
        return direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
    }

    // Get all products with pagination
        public Page<Product> getAllProducts(int page, int size, String sortBy, String direction) {

        Pageable pageable = PageRequest.of(page,size,createSort(sortBy,direction));
        return productRepository.findAll(pageable);
    }


    public Page<Product> getProductsByCategory(String category, int page, int size, String sortBy, String direction) {

        Pageable pageable = PageRequest.of(page,size,createSort(sortBy,direction));
       // Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Category c = categoryReposotory.findByName(category).orElseThrow(()->new BusinessException(HttpStatus.NOT_FOUND, "Category not found") );
            return productRepository.findByCategory(c,pageable);

    }

    public Page<Product> searchProducts(String search, int page, int size, String sortBy, String direction) {


        Pageable pageable = PageRequest.of(page,size,createSort(sortBy,direction));

        return productRepository.searchProductBy(search,pageable);
    }

    public ProductResponseDTO updateImage(MultipartFile file,long id) throws RuntimeException,IOException {
          Product p =  productRepository.findById(id).
                  orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Product not found") );
          String url = storageService.uploadFile(file);
          p.setImageURL(url);
          return toProductResponse(productRepository.save(p));
    }

    public ProductResponseDTO createProduct(ProduitDto produitDto, MultipartFile file) throws Exception{
        Category c = categoryReposotory.findById(produitDto.getCategory().getId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Category not found") );
        Product product = new Product(produitDto.getName(),produitDto.getPrice(),produitDto.getDescription(),produitDto.getStock(),c);
        if (file != null && !file.isEmpty()) {
            String url = storageService.uploadFile(file);
            product.setImageURL(url);
        }else {            throw new BusinessException(HttpStatus.BAD_REQUEST, "Product Image not found");
        }
        product = productRepository.save(product);
        return toProductResponse(product);
    }
    public ProductResponseDTO updateProduct(Long id, ProduitDto produitDto){

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Product not found"));

        Category category = categoryReposotory.findById(produitDto.getCategory().getId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Category not found"));

        existingProduct.setName(produitDto.getName());
        existingProduct.setPrice(produitDto.getPrice());
        existingProduct.setDescription(produitDto.getDescription());
        existingProduct.setStock(produitDto.getStock());
        existingProduct.setCategory(category);

        productRepository.save(existingProduct);

        return toProductResponse(existingProduct);
    }

    public ProductResponseDTO updateStock(int stock , Long id){
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Product not found") );
        p.setStock(stock);
        productRepository.save(p);
        return toProductResponse(p);
    }

    private ProductResponseDTO toProductResponse(Product product){
            return ProductResponseDTO.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .description(product.getDescription())
                    .imageURL(product.getImageURL())
                    .category(product.getCategory() != null
                            ? CategoryDTO.builder()
                                .id(product.getCategory().getId())
                                .name(product.getCategory().getName())
                                .build()
                            : null)
                    .stock(product.getStock())
                    .build();
    }
}