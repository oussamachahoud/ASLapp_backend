package com.example.aslapp_backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter @Setter
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String street;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String wilaya;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String commune;

    @NotBlank
    @Size(max = 10)
    @Column(name = "code_postal", nullable = false, length = 10)
    private String codePostal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private user user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    // Constructors
    public Address() {}

    public Address(String street, String wilaya, String commune, String codePostal, user user) {
        this.street = street;
        this.wilaya = wilaya;
        this.commune = commune;
        this.codePostal = codePostal;
        this.user = user;
    }
}
