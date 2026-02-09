package com.example.aslapp_backend.controller;
import com.example.aslapp_backend.DTOs.ProductResponseDTO;
import com.example.aslapp_backend.DTOs.ProduitDto;
import com.example.aslapp_backend.models.Product;
import com.example.aslapp_backend.sevices.ProductService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/api/products")
public class ProduitController {

    @Autowired
    private ProductService productService;



    public ProduitController() {
    }


    @GetMapping
    public ResponseEntity<Page<Product>> getAllProduct(
            @RequestParam(defaultValue =  "0") int page,
            @RequestParam(defaultValue =  "10") int size,
           @RequestParam(defaultValue =  "id") String sortBy,
          @RequestParam(defaultValue = "asc") String direction){

        Page<Product> products = productService.getAllProducts(page, size, sortBy, direction);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Page<Product>> getByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue =  "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Page<Product> products = productService.getProductsByCategory(category, page, size,sortBy,direction);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Product>> searchProducts(
            @RequestParam("q") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue =  "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ){
        Page<Product> products = productService.searchProducts(search, page, size,sortBy,direction);
        return ResponseEntity.ok(products);
    }

    @PostMapping("/{id}/update-image")
    public ResponseEntity<String> updateImage(
           @PathVariable long id,
           @RequestParam("file") MultipartFile file
    ){
        try {
            Product p = productService.updateImage(file,id);
            return ResponseEntity.ok("Image uploaded successfully. New URL: " + p.getImageURL());
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);

        }catch (IOException e){
            return ResponseEntity.status(505).body("Failed to upload image: " + e.getMessage());
        }

    }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SELLER')")
    @PostMapping(value = "/add-produit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addProduit(
            @RequestPart("produit") @Valid ProduitDto produitDto,
            @RequestPart(value = "file", required = false) MultipartFile file
            ){

        try {
            ProductResponseDTO product = productService.createProduct(produitDto, file);
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create("/api/products/" + product.getId()));
            headers.set("X-Entity-Version", "1.0");

            return new ResponseEntity<>(product, headers, HttpStatus.CREATED);
        } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create product: " + e.getMessage());
        }


    }



}