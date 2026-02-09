package com.example.aslapp_backend.controller;

import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import com.example.aslapp_backend.DTOs.AddressResponseDTO;
import com.example.aslapp_backend.DTOs.UpdateUserDTO;
import com.example.aslapp_backend.DTOs.userDTO;
import com.example.aslapp_backend.DTOs.userWithAddressResponseDTO;
import com.example.aslapp_backend.models.user;
import com.example.aslapp_backend.sevices.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("/me")
    public ResponseEntity<?> patchMe(
            @AuthenticationPrincipal user currentUser,
            @Valid @RequestBody UpdateUserDTO dto,
            BindingResult bindingResult
    ) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(err ->
                    errors.put(err.getField(), err.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors);
        }

        user updated = userService.patchMe(currentUser.getId(), dto);

        List<AddressResponseDTO> addresses = userService.getUserWithAdressById(updated.getId()).getAddress().stream()
                .map(a -> new AddressResponseDTO(a.getId(), a.getStreet(), a.getWilaya(), a.getCommune(), a.getCodePostal()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new userWithAddressResponseDTO(
                updated.getId(),
                updated.getUsername(),
                updated.getEmail(),
                updated.getAge(),
                updated.getImageURL(),
                addresses
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<userWithAddressResponseDTO> me(@AuthenticationPrincipal user user) {
        List<AddressResponseDTO> addresses = userService.getUserWithAdressById(user.getId()).getAddress().stream()
                .map(a -> new AddressResponseDTO(a.getId(), a.getStreet(), a.getWilaya(), a.getCommune(), a.getCodePostal()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new userWithAddressResponseDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getAge(),
                        user.getImageURL(),
                        addresses
                )
        );
    }

    @PostMapping("/{id}/update-image")
    public ResponseEntity<String> updateImage(
            @PathVariable long id,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            user u = userService.updateImage(file, id);
            return ResponseEntity.ok("Image uploaded successfully. New URL: " + u.getImageURL());
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image: " + e.getMessage());
        }

    }

    @DeleteMapping("/users/me")
    public ResponseEntity<String> deleteMyAccunte(
            @AuthenticationPrincipal user user

    ) {
        try {
            userService.deleteUser(user.getId());
            return ResponseEntity.status(200).body("User deletes");
        }catch (Exception e){
            return ResponseEntity.status(404).body("User not found");
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(
        @PathVariable long id
    ) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.status(200).body("User deletes");
        }catch (Exception e){
            return ResponseEntity.status(404).body("User not found");
        }
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/alluser")
    public ResponseEntity<Page<userDTO>> getAlluser(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size,
                                                                       @RequestParam(defaultValue =  "id") String sortBy,
                                                                       @RequestParam(defaultValue = "asc") String direction) {
        Page<userDTO> userDTOPage =userService.getAlluser(page, size,sortBy,direction);
        return ResponseEntity.ok(
                userDTOPage
        );
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("alluserAdress")
    public ResponseEntity<Page<userWithAddressResponseDTO>> getAlluserWithAdress(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size,
                                                                       @RequestParam(defaultValue =  "id") String sortBy,
                                                                       @RequestParam(defaultValue = "asc") String direction) {
        Page<userWithAddressResponseDTO> userDTOPage =userService.getAlluserWithAdress(page, size,sortBy,direction);
        return ResponseEntity.ok(
                userDTOPage
        );
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/users/{id}")
    public ResponseEntity<?> findUser(
            @PathVariable long id
    ) {
        try {
            userDTO user = userService.findUser(id);
            return ResponseEntity.ok().body(user);
        }catch (Exception e){
            return ResponseEntity.status(404).body("User not found");
        }
    }


}