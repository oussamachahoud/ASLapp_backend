package com.example.aslapp_backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import com.example.aslapp_backend.models.Product;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor // Added to fix the error
public  class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    private String name;
    public Category(String name){
        this.name=name;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "category")

    private List<Product> product = new ArrayList<>();

    public void addProduct(Product product) {
        product.setCategory(this);  // update FK
        this.product.add(product);  // maintain collection
    }

    public void removeProduct(Product product) {
        this.product.remove(product);
        product.setCategory(null);   // remove FK
    }


}