package com.example.aslapp_backend.sevices;


import com.example.aslapp_backend.DTOs.ProductResponseDTO;
import com.example.aslapp_backend.DTOs.ProduitDto;
import com.example.aslapp_backend.Exeption.BusinessException;
import com.example.aslapp_backend.models.Product;
import com.example.aslapp_backend.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

            return productRepository.findByCategory(category,pageable);

    }

    public Page<Product> searchProducts(String search, int page, int size, String sortBy, String direction) {


        Pageable pageable = PageRequest.of(page,size,createSort(sortBy,direction));

        return productRepository.searchProductBy(search,pageable);
    }

    public Product updateImage(MultipartFile file,long id) throws RuntimeException,IOException {
          Product p =  productRepository.findById(id).
                  orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Produit not found") );
          String url = storageService.uploadFile(file);
          p.setImageURL(url);
          return productRepository.save(p);
    }

    public ProductResponseDTO createProduct(ProduitDto produitDto, MultipartFile file) throws Exception{
        Product product = new Product(produitDto.getName(),produitDto.getPrice(),produitDto.getDescription(),produitDto.getStock(),produitDto.getCategory());
        if (file != null && !file.isEmpty()) {
            String url = storageService.uploadFile(file);
            product.setImageURL(url);
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Produit Image not found");
        }
        product = productRepository.save(product);
        return toProductResponse(product);
    }
    public Product updateProduct(ProduitDto produitDto){
        Product product = new Product(produitDto.getName(),produitDto.getPrice(),produitDto.getDescription(),produitDto.getStock(),produitDto.getCategory());
        return productRepository.save(product);
    }

    private ProductResponseDTO toProductResponse(Product product){
            return ProductResponseDTO.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .description(product.getDescription())
                    .imageURL(product.getImageURL())
                    .category(product.getCategory())
                    .stock(product.getStock())
                    .build();
    }
}