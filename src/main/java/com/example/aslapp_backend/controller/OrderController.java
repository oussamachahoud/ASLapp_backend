package com.example.aslapp_backend.controller;

import com.example.aslapp_backend.DTOs.requestDTOs.PlaceOrderDTO;
import com.example.aslapp_backend.DTOs.requestDTOs.UpdateOrderStatusDTO;
import com.example.aslapp_backend.DTOs.responseDTOs.OrderItemResponseDTO;
import com.example.aslapp_backend.DTOs.responseDTOs.OrderResponseDTO;
import com.example.aslapp_backend.DTOs.responseDTOs.AddressResponseDTO;
import com.example.aslapp_backend.Exeption.BusinessException;
import com.example.aslapp_backend.models.Address;
import com.example.aslapp_backend.models.Order;
import com.example.aslapp_backend.models.User;
import com.example.aslapp_backend.sevices.OrderService;
import com.example.aslapp_backend.sevices.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Orders", description = "Place, view and manage orders")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    /**
     * Place an order from the User's cart
     * POST /api/orders/place
     * AUTH required
     */
    @Operation(summary = "Place an order", description = "Creates an order from the user's current cart")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order placed successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Shipping address not found")
    })
    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody PlaceOrderDTO placeOrderDTO
    ) {
        try {
            // Get User with addresses
            User userWithAddresses = userService.getUserWithAdressById(currentUser.getId());

            // Find the shipping address
            Address shippingAddress = userWithAddresses.getAddress().stream()
                    .filter(addr -> addr.getId().equals(placeOrderDTO.getShippingAddressId()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Shipping address not found"));

            // Place the order
            Order order = orderService.placeOrder(userWithAddresses, shippingAddress, placeOrderDTO.getPaymentMethod());

            // Convert to DTO
            OrderResponseDTO orderDTO = mapToOrderResponseDTO(order);

            return ResponseEntity.status(HttpStatus.CREATED).body(orderDTO);
        } catch (BusinessException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error placing order: " + e.getMessage());
        }
    }

    /**
     * Get all orders for the authenticated User
     * GET /api/orders
     * AUTH required
     */
    @Operation(summary = "List my orders", description = "Returns a paginated list of the authenticated user's orders")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of orders returned"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping
    public ResponseEntity<?> getUserOrders(
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        try {
            Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

            Page<Order> orders = orderService.getUserOrders(currentUser, pageable);
            Page<OrderResponseDTO> ordersDTO = orders.map(this::mapToOrderResponseDTO);

            return ResponseEntity.ok(ordersDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid sort direction. Use 'asc' or 'desc'.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving orders: " + e.getMessage());
        }
    }

    /**
     * Get order details by ID
     * GET /api/orders/{id}
     * AUTH required + owner/admin check
     */
    @Operation(summary = "Get order details", description = "Returns details of a specific order (owner or admin)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order details returned"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not the order owner"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetails(
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser,
            @PathVariable Long id
    ) {
        try {
            // Get order and check if User is owner
            Order order = orderService.getOrderByIdForUser(id, currentUser);
            OrderResponseDTO orderDTO = mapToOrderResponseDTO(order);

            return ResponseEntity.ok(orderDTO);
        } catch (BusinessException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving order: " + e.getMessage());
        }
    }

    /**
     * Update order status (admin only)
     * PUT /api/admin/orders/{id}/status
     * ROLE_ADMIN required
     */
    @Operation(summary = "Update order status (Admin)", description = "Updates the status of an order – requires ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order status updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorised (admin only)"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PutMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusDTO updateStatusDTO
    ) {
        try {
            Order updatedOrder = orderService.updateOrderStatus(id, updateStatusDTO.getStatus());
            OrderResponseDTO orderDTO = mapToOrderResponseDTO(updatedOrder);

            return ResponseEntity.ok(orderDTO);
        } catch (BusinessException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating order status: " + e.getMessage());
        }
    }

    /**
     * Helper method to convert Order entity to OrderResponseDTO
     */
    private OrderResponseDTO mapToOrderResponseDTO(Order order) {
        AddressResponseDTO addressDTO = new AddressResponseDTO(
                order.getShippingAddress().getId(),
                order.getShippingAddress().getStreet(),
                order.getShippingAddress().getWilaya(),
                order.getShippingAddress().getCommune(),
                order.getShippingAddress().getCodePostal()
        );

        java.util.List<OrderItemResponseDTO> itemDTOs = order.getOrderItem().stream()
                .map(item -> OrderItemResponseDTO.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitprice())
                        .subtotal(item.getQuantity() * item.getUnitprice())
                        .build())
                .toList();

        return OrderResponseDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .totalAmount(order.getTotalAmount())
                .status(order.getOderStatus())
                .paymentMethod(order.getPaymentMethod())
                .shippingAddress(addressDTO)
                .items(itemDTOs)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getModifiedAt())
                .build();
    }
}
