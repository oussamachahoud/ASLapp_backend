package com.example.aslapp_backend.models;


import com.example.aslapp_backend.Exeption.BusinessException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
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
    private String imageURL;


    @ManyToOne()
    @JoinColumn(name ="category_Id")
    private Category category;


    @Column
    @PositiveOrZero
    @NotNull
    private int stock;

    public void setCategory(Category category){
        this.category=category;
    }

    public Product(long id, String name, double price, String description, int stock,Category category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.stock = stock;
        this.category=category;
    }
    public Product( String name, double price, String description, int stock,Category category) {

        this.name = name;
        this.price = price;
        this.description = description;
        this.stock = stock;
        this.category=category;
    }
    public void decreaseStock(Integer quantity) {
        if (quantity > stock) {
            throw new BusinessException(HttpStatus.NOT_FOUND,"Insufficient stock");
        }
        this.stock -= quantity;
    }

    public void increaseStock(Integer quantity) {
        this.stock += quantity;
    }

//❌ REMOVED cartItemList and orderItemList. ( never
    // put these lists in the Product entity. It will kill your database performance).
   // @OneToMany(mappedBy = "product" ,cascade = CascadeType.ALL)
    //private List<OrderItem> OrderItemList = new ArrayList<>();

    //@OneToMany(mappedBy = "product" ,cascade = CascadeType.ALL)
    //private List<CartItem> cartItemList = new ArrayList<>();



}