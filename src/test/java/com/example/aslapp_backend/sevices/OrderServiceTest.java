package com.example.aslapp_backend.sevices;

import com.example.aslapp_backend.models.*;
import com.example.aslapp_backend.models.Enum.paymentMethod;
import com.example.aslapp_backend.repositories.CartRepository;
import com.example.aslapp_backend.repositories.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)

public class OrderServiceTest {

    @Mock
    private  OrderRepository orderRepository;
    @Mock
    private CartRepository cartRepository;
    @InjectMocks
    private OrderService orderService;

    @Test
  void  placeOrder_Test(){

        User user = new User("testuser", "password", "test@example.com", 25);
        Address shippingAddress = new Address("123 Main St", "Algiers", "Bab El Oued", "16000", user);
        paymentMethod paymentMethod = com.example.aslapp_backend.models.Enum.paymentMethod.CREDIT_CARD;
        Cart cart =new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setTotalPrice(BigDecimal.valueOf(1000));

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(new Product("xxxx",11111,"dizel",3,new Category()));
        cartItem.setQuantity(1);
        cartItem.setUnitPrice(135);
        cart.addItem(cartItem);

        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        // ensure orderRepository.save(...) returns the same order instance so placeOrder returns non-null
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = orderService.placeOrder(user,shippingAddress,paymentMethod);

        assertEquals(order.getUser(),user);
        verify(orderRepository).save(order);
        verify(cartRepository).save(cart);

  }

}