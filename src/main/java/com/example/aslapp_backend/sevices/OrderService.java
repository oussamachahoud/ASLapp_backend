package com.example.aslapp_backend.sevices;

import com.example.aslapp_backend.Exeption.BusinessException;
import com.example.aslapp_backend.models.*;
import com.example.aslapp_backend.models.Enum.OderStatus;
import com.example.aslapp_backend.models.Enum.paymentMethod;
import com.example.aslapp_backend.repositories.CartRepository;
import com.example.aslapp_backend.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    /**
     * Place an order from User's cart
     * @param user The User placing the order
     * @param shippingAddress The address to ship to
     * @param paymentMethod The payment method
     * @return The created order
     */
    public Order placeOrder(User user, Address shippingAddress, paymentMethod paymentMethod) {
        // Get User's cart
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Cart not found"));

        // Validate cart is not empty
        if (cart.getCartItemList() == null || cart.getCartItemList().isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Cart is empty. Cannot place order.");
        }

        // Create order
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setOderStatus(OderStatus.NEW);

        // Convert cart items to order items
        List<OrderItem> orderItems = cart.getCartItemList().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setProduct(cartItem.getProduct());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setUnitprice(cartItem.getProduct().getPrice());
                    orderItem.setOrder(order);
                    return orderItem;
                })
                .collect(Collectors.toList());

        order.setOrderItem(orderItems);
        order.recalculateTotals();

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Clear cart after successful order placement
        cart.getCartItemList().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setTotalItems(0);
        cartRepository.save(cart);

        return savedOrder;
    }

    /**
     * Get all orders for a User with pagination
     * @param user The User
     * @param pageable Pagination info
     * @return Page of orders
     */
    @Transactional(readOnly = true)
    public Page<Order> getUserOrders(User user, Pageable pageable) {
        return orderRepository.findByUser(user, pageable);
    }

    /**
     * Get a specific order by ID
     * @param orderId The order ID
     * @return The order if found
     */
    @Transactional(readOnly = true)
    public Order getOrderById(Long orderId) {
        return orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    /**
     * Get order by ID for a specific User (authorization check)
     * @param orderId The order ID
     * @param user The User requesting
     * @return The order if User is the owner
     */
    @Transactional(readOnly = true)
    public Order getOrderByIdForUser(Long orderId, User user) {
        Order order = getOrderById(orderId);
        
        // Check if User is the owner or has admin role
        if (!order.getUser().getId().equals(user.getId())) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "You do not have permission to access this order");
        }
        
        return order;
    }

    /**
     * Update order status (admin only)
     * @param orderId The order ID
     * @param newStatus The new status
     * @return The updated order
     */
    public Order updateOrderStatus(Long orderId, OderStatus newStatus) {
        Order order = getOrderById(orderId);
        order.setOderStatus(newStatus);
        return orderRepository.save(order);
    }

    /**
     * Generate a unique order number
     * @return Generated order number
     */
    private String generateOrderNumber() {
        return "ORDER-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
