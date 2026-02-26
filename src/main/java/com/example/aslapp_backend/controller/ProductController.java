package com.example.aslapp_backend.controller;
import com.example.aslapp_backend.DTOs.responseDTOs.ProductResponseDTO;
import com.example.aslapp_backend.DTOs.requestDTOs.ProduitDto;
import com.example.aslapp_backend.models.Product;
import com.example.aslapp_backend.sevices.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;

@Tag(name = "Products", description = "Browse, create and manage products")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {


    private final  ProductService productService;
    private final  ObjectMapper objectMapper;


    @Operation(summary = "List all products", description = "Returns a paginated list of all products")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of products returned")
    })
    @GetMapping
    public ResponseEntity<Page<Product>> getAllProduct(
            @RequestParam(defaultValue =  "0") int page,
            @RequestParam(defaultValue =  "10") int size,
           @RequestParam(defaultValue =  "id") String sortBy,
          @RequestParam(defaultValue = "asc") String direction){

        Page<Product> products = productService.getAllProducts(page, size, sortBy, direction);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "List products by category", description = "Returns a paginated list of products filtered by category name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of products returned")
    })
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

    @Operation(summary = "Search products", description = "Full-text search on product name/description")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of matching products returned")
    })
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

    @Operation(summary = "Update product image", description = "Uploads a new image for the given product")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "500", description = "Failed to upload image")
    })
    @PostMapping("/{id}/update-image")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<String> updateImage(
           @PathVariable long id,
           @RequestParam("file") MultipartFile file
    ){
        try {
            ProductResponseDTO p = productService.updateImage(file,id);
            return ResponseEntity.ok("Image uploaded successfully. New URL: " + p.getImageURL());
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);

        }catch (IOException e){
            return ResponseEntity.status(505).body("Failed to upload image: " + e.getMessage());
        }

    }
    @Operation(summary = "Add a new product (Admin/Seller)", description = "Creates a new product with optional image upload")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Product created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorised (admin/seller only)"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @PostMapping(value = "/add-produit",  consumes = "multipart/form-data")
    public ResponseEntity<?> addProduit(
            @Parameter(
                    description = "Product data as a JSON string (Content-Type will be resolved automatically)",
                    required = true,
                    schema = @Schema(type = "string",
                            example = "{\"name\":\"ASL T-Shirt\",\"price\":29.99,\"description\":\"High quality cotton T-Shirt\",\"category\":{\"id\":1,\"name\":\"Clothing\"},\"stock\":100}")
            )
            @RequestPart("produit") String produitJson,
            @RequestPart(value = "file", required = false) MultipartFile file
            ){

        try {
            ProduitDto produitDto = objectMapper.readValue(produitJson, ProduitDto.class);
            ProductResponseDTO product = productService.createProduct(produitDto, file);
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create("/api/products/" + product.getId()));
            headers.set("X-Entity-Version", "1.0");

            return new ResponseEntity<>(product, headers, HttpStatus.CREATED);
        } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create product: " + e.getMessage());
        }


    }

    @Operation(summary = "Update a product (Admin/Seller)", description = "Updates product details by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorised (admin/seller only)"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @SneakyThrows
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProduitDto updateProduit) {

        ProductResponseDTO updated = productService.updateProduct(id, updateProduit);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Update product stock (Admin/Seller)", description = "Adjusts the stock quantity of a product")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock updated"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorised (admin/seller only)"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @SneakyThrows
    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<ProductResponseDTO> updateStock(
            @PathVariable Long id,
            @RequestParam int stock) {

        ProductResponseDTO updated = productService.updateStock(stock, id);
        return ResponseEntity.ok(updated);
    }




}