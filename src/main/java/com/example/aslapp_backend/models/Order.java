package com.example.aslapp_backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "orders")
public class Order extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;


    @Column(length = 50)
    private String status;

    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderList = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "user_id")
    private user user;


    @ManyToOne
    @JoinColumn(name = "shipping_address_id", nullable = false)
    private Address shippingAddress;


    public Order() {
        this.totalAmount = BigDecimal.ZERO;
        this.status = "NEW";
    }
    public void recalculateTotals() {
        this.totalAmount = orderList.stream()
                .map(orderItem ->
                        BigDecimal.valueOf(orderItem.getQuantity())
                                .multiply(BigDecimal.valueOf(orderItem.getUnitprice()))
                ).reduce(BigDecimal.ZERO,BigDecimal::add);}
    // Convenience constructor
    public Order(String orderNumber, BigDecimal totalAmount, String status, String paymentMethod) {
        this.orderNumber = orderNumber;
        this.totalAmount = totalAmount == null ? BigDecimal.ZERO : totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }



    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderNumber='" + orderNumber + '\'' +
                ", totalAmount=" + totalAmount +
                ", createdAt=" + getCreatedAt() +
                ", status='" + status + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}