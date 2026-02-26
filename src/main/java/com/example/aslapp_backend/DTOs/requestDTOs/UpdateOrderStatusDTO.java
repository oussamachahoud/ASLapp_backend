package com.example.aslapp_backend.DTOs.requestDTOs;

import com.example.aslapp_backend.models.Enum.OderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Request body for updating an order's status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusDTO {
    @Schema(description = "New order status", example = "SHIPPED")
    @NotNull(message = "Order status is required")
    private OderStatus status;
}
