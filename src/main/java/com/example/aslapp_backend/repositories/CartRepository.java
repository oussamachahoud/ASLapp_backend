package com.example.aslapp_backend.repositories;
import com.example.aslapp_backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.aslapp_backend.models.Cart;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {
     @Query("SELECT c FROM Cart c " +
             "LEFT JOIN FETCH c.user u " +
             "LEFT JOIN FETCH c.cartItemList ci " +
             "LEFT JOIN FETCH ci.product p " +
             "WHERE u = :User ")
     Optional<Cart> findByUser(@Param("User") User user) ;
}
