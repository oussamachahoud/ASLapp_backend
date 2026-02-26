package com.example.aslapp_backend.DTOs.responseDTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Single item inside a cart")
@Builder
@Getter
@Setter
@AllArgsConstructor
public class CartItemDTO {
    @Schema(description = "Cart item ID", example = "1")
    private Long id;
    @Schema(description = "Quantity", example = "2")
    private int quantity;
    @Schema(description = "Unit price", example = "29.99")
    private double unitPrice;
    @Schema(description = "Product ID", example = "10")
    private Long productId;
    @Schema(description = "Product name", example = "ASL T-Shirt")
    private String productName;
    @Schema(description = "Product image URL", example = "https://storage.example.com/img.jpg")
    private String productImage;
    @Schema(description = "Subtotal (quantity × unitPrice)", example = "59.98")
    private double subtotal;
}
