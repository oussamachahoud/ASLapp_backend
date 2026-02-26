package com.example.aslapp_backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import com.fasterxml.jackson.annotation.JsonIgnore; // Import needed for API cleanups
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "cart_items")
@Setter @Getter
@NoArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @PositiveOrZero
    @NotNull
    private int quantity;

    @NotNull
    @PositiveOrZero
    private double unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    @JsonIgnore
    private Cart cart;

    public CartItem(Cart cart, Product product, double unitPrice, int quantity) {
        this.cart = cart;
        this.product = product;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    // --- HELPER ---
    public double getTotalPrice() {
        return this.quantity * this.unitPrice;
    }


}
