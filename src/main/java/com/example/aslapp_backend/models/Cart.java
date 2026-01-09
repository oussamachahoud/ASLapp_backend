package com.example.aslapp_backend.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Cart extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private long id;

    @PositiveOrZero
    @Column(nullable = false, precision = 12, scale = 2)
    @Getter
    @Setter
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @PositiveOrZero
    @Column(nullable = false)
    @Getter
    @Setter
    private int totalItems = 0;

    @OneToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",referencedColumnName = "id",unique = true)
    @Getter
    @Setter
    private user user;


    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    @Getter
    @Setter
    private List<CartItem> cartItemList = new ArrayList<>();;



    public void setCartItemList(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList == null ? new ArrayList<>() : cartItemList;
        recalculateTotals();
    }

    // helpers
    public void addItem(CartItem item) {
        item.setCart(this);
        cartItemList.add(item);
        recalculateTotals();

    }
    public void removeItem(CartItem item){
    if (cartItemList.remove(item)){
        item.setCart(null);
        recalculateTotals();
    }
    }

    public void recalculateTotals() {
        this.totalPrice = cartItemList.stream()
                .map(item -> {
                    // Convert Item price (double) to BigDecimal for safe math
                    BigDecimal price = BigDecimal.valueOf(item.getUnitPrice());
                    BigDecimal qty = BigDecimal.valueOf(item.getQuantity());
                    return price.multiply(qty);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalItems = cartItemList.stream().mapToInt(CartItem::getQuantity).sum();

    }

}
