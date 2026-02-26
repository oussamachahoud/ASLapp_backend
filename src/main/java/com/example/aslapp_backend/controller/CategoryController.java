package com.example.aslapp_backend.controller;


import com.example.aslapp_backend.DTOs.requestDTOs.CategoryRequestDTO;
import com.example.aslapp_backend.DTOs.responseDTOs.CategoryDTO;
import com.example.aslapp_backend.sevices.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name ="Category",description = "Browse, create and manage category")

@RestController
@RequestMapping("api/category")
// generates a constructor for all final fields and fields marked with @NonNull
@RequiredArgsConstructor
public class CategoryController {

    final private CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "create category", description = "Returns a CategoryDTO")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "CategoryDTO returned")
    })
    public ResponseEntity<?> createCategory(
            CategoryRequestDTO categoryRequestDTO
            ){
       CategoryDTO categoryDTO = categoryService.create(categoryRequestDTO.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(categoryDTO);


    }
    @Operation(summary = "delete category", description = "category {id} had delete")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "category {id} had delete")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @Parameter(description="Category ID", example="1")
          @PathVariable  Long id) {

            categoryService.delete(id);



        return ResponseEntity.ok (   "Deletes a category by its ID")
        ;
    }

    @PatchMapping("{id}")
    @Operation(summary = "Patch category", description = "Returns a CategoryDTO")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "CategoryDTO returned")
    })
    @PreAuthorize("hasRole('ADMIN')")
     public ResponseEntity<?> ParchCategory(
            CategoryRequestDTO categoryRequestDTO
,
            @Parameter(description="Category ID", example="1")@PathVariable  Long id
    ){
        CategoryDTO categoryDTO = categoryService.Patch(id,categoryRequestDTO.getName());

        return ResponseEntity.ok(categoryDTO);


    }
    @GetMapping("/all")
    public ResponseEntity<?>getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue =  "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ){
        Sort s =direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page,size,s);
        Page<CategoryDTO> p= categoryService.findAll(pageable);
        return ResponseEntity.ok(p);


    }



}
