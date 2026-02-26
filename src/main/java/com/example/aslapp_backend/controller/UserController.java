package com.example.aslapp_backend.controller;

import com.example.aslapp_backend.models.Enum.ERole;
import com.example.aslapp_backend.models.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import com.example.aslapp_backend.DTOs.responseDTOs.AddressResponseDTO;
import com.example.aslapp_backend.DTOs.requestDTOs.AddressRequestDTO;
import com.example.aslapp_backend.DTOs.requestDTOs.UpdateUserDTO;
import com.example.aslapp_backend.DTOs.modelDTOs.userDTO;
import com.example.aslapp_backend.DTOs.responseDTOs.userWithAddressResponseDTO;
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
import java.util.Set;
import java.util.stream.Collectors;

@Tag(name = "Users", description = "User profile, addresses, admin user management")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Update my profile", description = "Partially updates the authenticated user's profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PatchMapping("/me")
    public ResponseEntity<?> patchMe(
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser,
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

        userDTO updated = userService.patchMe(currentUser.getId(), dto);



        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Get my profile", description = "Returns the authenticated user's profile with addresses")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile returned"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping("/me")
    public ResponseEntity<userWithAddressResponseDTO> me(@Parameter(hidden = true) @AuthenticationPrincipal User user) {
        Set<AddressResponseDTO> addresses = userService.getUserWithAdressById(user.getId()).getAddress().stream()
                .map(a -> new AddressResponseDTO(a.getId(), a.getStreet(), a.getWilaya(), a.getCommune(), a.getCodePostal()))
                .collect(Collectors.toSet());

        return ResponseEntity.ok(
                userWithAddressResponseDTO.builder()
                        .id(user.getId())
                        .age(user.getAge())
                        .email(user.getEmail())
                        .imageURL(user.getImageURL())
                        .username(user.getUsername())
                        .addresses(addresses)
                        .role(user.getRoles().stream().map(i->i.getName().toString()).collect(Collectors.toSet()))
                        .build()
        );
    }

    @Operation(summary = "Add an address", description = "Adds a new address to the authenticated user's profile")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Address created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PostMapping("/me/address")
    public ResponseEntity<AddressResponseDTO> addAddress(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Valid @RequestBody AddressRequestDTO addressRequestDTO
    ) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        AddressResponseDTO addressResponseDTO = userService.addAddressForUser(user.getId(), addressRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(addressResponseDTO);
    }

    @DeleteMapping("/me/address/{id}")
    public ResponseEntity<String> deleteAddress(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "Identify the address that should  be deleted ",
            schema = @Schema(type ="Long",
            example = "1")
            )
            @PathVariable Long addressId
    ) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

         userService.removeAddressForUser(user,addressId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("the address was delete");
    }

    @Operation(summary = "Update user image", description = "Uploads a new profile image for the given user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Failed to upload image")
    })
    @PostMapping("/{id}/update-image")
    public ResponseEntity<String> updateImage(
            @Parameter(description="uder ID", example="1")@PathVariable long id,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            User u = userService.updateImage(file, id);
            return ResponseEntity.ok("Image uploaded successfully. New URL: " + u.getImageURL());
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image: " + e.getMessage());
        }

    }

    @Operation(summary = "Delete my account", description = "Deletes the authenticated user's account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deleted"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteMyAccunte(
            @Parameter(hidden = true) @AuthenticationPrincipal User user

    ) {
        try {
            userService.deleteUser(user.getId());
            return ResponseEntity.status(200).body("User deletes");
        }catch (Exception e){
            return ResponseEntity.status(404).body("User not found");
        }
    }

    @Operation(summary = "Delete a user (Admin)", description = "Deletes a user by ID – requires ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deleted"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorised (admin only)"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/Delete/{id}")
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
    @Operation(summary = "List all users (Admin)", description = "Returns a paginated list of all users – requires ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of users returned"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorised (admin only)")
    })
    @PreAuthorize("hasRole('ADMIN')")
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
    @Operation(summary = "List all users with addresses (Admin)", description = "Returns a paginated list of users including their addresses – requires ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of users with addresses returned"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorised (admin only)")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/users-with-addresses")
    public ResponseEntity<Page<userWithAddressResponseDTO>> getAlluserWithAdress(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size,
                                                                       @RequestParam(defaultValue =  "id") String sortBy,
                                                                       @RequestParam(defaultValue = "asc") String direction) {
        Page<userWithAddressResponseDTO> userDTOPage =userService.getAlluserWithAdress(page, size,sortBy,direction);
        return ResponseEntity.ok(
                userDTOPage
        );
    }
    @Operation(summary = "Find a user by ID (Admin)", description = "Returns user details by ID – requires ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorised (admin only)"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/find/{id}")
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

    @Operation(summary = "Set user role (Admin)", description = "Changes a user's role – requires ADMIN role. Payload: {\"role\": \"ROLE_SELLER\"}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role updated"),
            @ApiResponse(responseCode = "400", description = "Missing or invalid role"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorised (admin only)")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/setrole/{id}")
    public ResponseEntity<?> setUserRole(
            @PathVariable long id,
            // {"role" : "ROLE_SELLER" } or {"role" : "ROLE_ADMIN" } or {"role" : "ROLE_USER" }
            @Parameter(
                    name = "role",
                    description = "Role of the user",
                    examples = {
                            @ExampleObject(name = "Admin Role", value = "ROLE_ADMIN"),
                            @ExampleObject(name = "User Role", value = "ROLE_USER")
                    }
            )            @RequestBody(required = true) Map<String,String> payload
            ){
        String role = payload == null ? null : payload.get("role");
        if (role == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing 'role' field");
        ERole eRole;
        try {
             eRole = ERole.valueOf(role);
        }catch (IllegalArgumentException ex )
        {return ResponseEntity.badRequest().body("Invalid role value");}
        userDTO user = userService.updateUserRole(id, eRole);
        return ResponseEntity.ok(user);
    }


}