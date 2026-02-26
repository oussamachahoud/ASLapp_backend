package com.example.aslapp_backend.DTOs.responseDTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Shopping cart overview")
@Builder
@Getter
@Setter
@AllArgsConstructor
public class CartResponseDTO {
    @Schema(description = "Cart ID", example = "1")
    private Long id;
    @Schema(description = "Total price of all items", example = "149.97")
    private BigDecimal totalPrice;
    @Schema(description = "Total number of items", example = "3")
    private int totalItems;
    @Schema(description = "List of cart items")
    private List<CartItemDTO> items;
}
