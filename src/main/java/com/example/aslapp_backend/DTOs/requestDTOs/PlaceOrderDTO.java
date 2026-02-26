package com.example.aslapp_backend.DTOs.requestDTOs;

import com.example.aslapp_backend.models.Enum.paymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Request body for placing an order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderDTO {
    @Schema(description = "ID of the shipping address", example = "1")
    @NotNull(message = "Shipping address ID is required")
    private Long shippingAddressId;

    @Schema(description = "Payment method", example = "CASH_ON_DELIVERY")
    @NotNull(message = "Payment method is required")
    private paymentMethod paymentMethod;
}
