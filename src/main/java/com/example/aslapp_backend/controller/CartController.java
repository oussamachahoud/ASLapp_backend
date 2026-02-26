package com.example.aslapp_backend.controller;

import com.example.aslapp_backend.DTOs.responseDTOs.CartResponseDTO;
import com.example.aslapp_backend.DTOs.responseDTOs.CartItemDTO;
import com.example.aslapp_backend.models.Cart;
import com.example.aslapp_backend.models.User;
import com.example.aslapp_backend.sevices.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cart", description = "Shopping-cart management")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    final private CartService cartService;

    @Operation(summary = "Add item to cart", description = "Adds a product to the authenticated user's cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item added – updated cart returned"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PostMapping("add")
    public ResponseEntity<?> addToCart(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @NotNull Long idProduct,
            @NotNull int quantity
    ){
        Cart cart = cartService.addTOCart(user, idProduct, quantity);
        return ResponseEntity.ok().body(mapToCartResponseDTO(cart));
    }

    @Operation(summary = "View cart", description = "Returns the authenticated user's current cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart returned"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping
    public ResponseEntity<?> viewCart(
            @Parameter(hidden = true) @AuthenticationPrincipal User user
    ){
        Cart cart = cartService.viewCart(user);
        return ResponseEntity.ok().body(mapToCartResponseDTO(cart));
    }

    @Operation(summary = "Remove item from cart", description = "Removes a cart item by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item removed – updated cart returned"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Cart item not found")
    })
    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<?> removeItem(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @PathVariable Long itemId
    ){
        Cart cart = cartService.removeItem(user, itemId);
        return ResponseEntity.ok().body(mapToCartResponseDTO(cart));
    }

    /**
     * Helper method to convert Cart entity to CartResponseDTO
     */
    private CartResponseDTO mapToCartResponseDTO(Cart cart) {
        java.util.List<CartItemDTO> itemDTOs = cart.getCartItemList().stream()
                .map(item -> CartItemDTO.builder()
                        .id(item.getId())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .productImage(item.getProduct().getImageURL())
                        .subtotal(item.getQuantity() * item.getUnitPrice())
                        .build())
                .toList();

        return CartResponseDTO.builder()
                .id(cart.getId())
                .totalPrice(cart.getTotalPrice())
                .totalItems(cart.getTotalItems())
                .items(itemDTOs)
                .build();
    }
}
