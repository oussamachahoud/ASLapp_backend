package com.example.aslapp_backend.repositories;

import com.example.aslapp_backend.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {

    @Override
    Optional<CartItem> findById(Long aLong);
}
