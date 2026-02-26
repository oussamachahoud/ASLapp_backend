package com.example.aslapp_backend.repositories;

import com.example.aslapp_backend.models.Order;
import com.example.aslapp_backend.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.OrderItem oi " +
            "LEFT JOIN FETCH oi.product " +
            "WHERE o.user = :User " +
            "ORDER BY o.createdAt DESC")
    Page<Order> findByUser(@Param("User") User user, Pageable pageable);

    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.OrderItem oi " +
            "LEFT JOIN FETCH oi.product " +
            "WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);

    @Query("SELECT o FROM Order o WHERE o.user = :User AND o.id = :id")
    Optional<Order> findByIdAndUser(@Param("id") Long id, @Param("User") User user);
}
