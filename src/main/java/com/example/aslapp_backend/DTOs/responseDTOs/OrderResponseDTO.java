package com.example.aslapp_backend.DTOs.responseDTOs;

import com.example.aslapp_backend.models.Enum.OderStatus;
import com.example.aslapp_backend.models.Enum.paymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Schema(description = "Order overview")
@Builder
@Getter
@Setter
@AllArgsConstructor
public class OrderResponseDTO {
    @Schema(description = "Order ID", example = "1")
    private Long id;
    @Schema(description = "Order number", example = "ORD-20260221-00001")
    private String orderNumber;
    @Schema(description = "Total amount", example = "149.97")
    private BigDecimal totalAmount;
    @Schema(description = "Order status", example = "NEW")
    private OderStatus status;
    @Schema(description = "Payment method", example = "CASH_ON_DELIVERY")
    private paymentMethod paymentMethod;
    @Schema(description = "Shipping address")
    private AddressResponseDTO shippingAddress;
    @Schema(description = "Order line items")
    private List<OrderItemResponseDTO> items;
    @Schema(description = "Creation timestamp")
    private Instant createdAt;
    @Schema(description = "Last update timestamp")
    private Instant updatedAt;
}
