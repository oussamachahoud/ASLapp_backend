package com.example.aslapp_backend.DTOs.responseDTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Single line item inside an order")
@Builder
@Getter
@Setter
@AllArgsConstructor
public class OrderItemResponseDTO {
    @Schema(description = "Order item ID", example = "1")
    private Long id;
    @Schema(description = "Product ID", example = "10")
    private Long productId;
    @Schema(description = "Product name", example = "ASL T-Shirt")
    private String productName;
    @Schema(description = "Quantity ordered", example = "2")
    private int quantity;
    @Schema(description = "Unit price at time of order", example = "29.99")
    private double unitPrice;
    @Schema(description = "Subtotal (quantity × unitPrice)", example = "59.98")
    private double subtotal;
}
