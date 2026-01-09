package com.example.aslapp_backend.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Product extends BaseEntity implements Serializable  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 125, nullable = false)
    private String name;

    @Column(nullable = false)
    private double price;

    @Column
    private String description;

    @Column
    @PositiveOrZero
    @NotNull
    private int stock;

    //❌ REMOVED cartItemList and orderItemList. (As discussed, never
    // put these lists in the Product entity. It will kill your database performance).
   // @OneToMany(mappedBy = "product" ,cascade = CascadeType.ALL)
    //private List<OrderItem> OrderItemList = new ArrayList<>();

    //@OneToMany(mappedBy = "product" ,cascade = CascadeType.ALL)
    //private List<CartItem> cartItemList = new ArrayList<>();


}