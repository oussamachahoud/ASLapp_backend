package com.example.aslapp_backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import com.fasterxml.jackson.annotation.JsonIgnore; // Import needed for API cleanups
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cart_items")
@Setter @Getter
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

    // --- HELPER ---
    public double getTotalPrice() {
        return this.quantity * this.unitPrice;
    }


}
