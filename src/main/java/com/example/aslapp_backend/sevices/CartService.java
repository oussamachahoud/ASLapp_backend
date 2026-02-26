package com.example.aslapp_backend.sevices;

import com.example.aslapp_backend.Exeption.BusinessException;
import com.example.aslapp_backend.models.Cart;
import com.example.aslapp_backend.models.CartItem;
import com.example.aslapp_backend.models.Product;
import com.example.aslapp_backend.models.User;
import com.example.aslapp_backend.repositories.CartItemRepository;
import com.example.aslapp_backend.repositories.CartRepository;
import com.example.aslapp_backend.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;


@Service
@Transactional
@AllArgsConstructor
public class CartService {

    final private CartRepository cartRepository;
    final private ProductRepository productRepository;
    final private CartItemRepository cartItemRepository;

    public Cart  viewCart(User user){
        return  cartRepository.findByUser(user)
                .orElseGet( () -> {
                    Cart c =new Cart();
                    c.setUser(user);
                    return cartRepository.save(c);
                });
    }

    public Cart addTOCart(User user, long idProduct, int quantity){

        Cart cart =cartRepository.findByUser(user)
                .orElseGet( () -> {
                        Cart c =new Cart();
                          c.setUser(user);
                          return cartRepository.save(c);
                });
        Product p =productRepository.findById(idProduct).orElseThrow(
                () -> new BusinessException(HttpStatus.NOT_FOUND, "Product not found")) ;
        if (p.getStock() < quantity) throw new BusinessException(HttpStatus.BAD_REQUEST, "Quantity not available");
        Optional<CartItem> existingItem = cart.getCartItemList()
                .stream()
                .filter((item) ->  Objects.equals(item.getProduct().getId(), p.getId()))
                .findFirst();
        if (existingItem.isPresent()){
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cart.recalculateTotals();
        }else {
        CartItem item =new CartItem(cart,p,(double) p.getPrice() *quantity,quantity);
            cart.addItem(item); }

        return  cartRepository.save(cart);
    }

    @Transactional
    public Cart removeItem(User user, Long idItem)
    {
        Cart cart =cartRepository.findByUser(user)
                .orElseGet( () -> {
                    Cart c =new Cart();
                    c.setUser(user);
                    return cartRepository.save(c);
                });
        Optional<CartItem> cartItem = cart.getCartItemList()
                .stream().
                filter( item ->Objects.equals(item.getId(),idItem) )
                .findFirst();
         if(!cartItem.isPresent()) throw new BusinessException(HttpStatus.NOT_FOUND, "Cart item not found");

        cart.removeItem(cartItem.get());

        return  cartRepository.save(cart);
    }

}
