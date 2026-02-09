package com.example.aslapp_backend.models;

import com.example.aslapp_backend.models.Enum.OderStatus;
import com.example.aslapp_backend.models.Enum.paymentMethod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "orders")

public class Order extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column( nullable = false, unique = true, length = 50)
    private String orderNumber;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;


    @Column( nullable = false)
    @Enumerated(value = EnumType.STRING)
    private OderStatus oderStatus;

    @Column( nullable = false, length = 50)
    @Enumerated(value = EnumType.STRING)
    private paymentMethod paymentMethod;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> OrderItem = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "user_id")
    private user user;


    @ManyToOne
    @JoinColumn(name = "shipping_address_id", nullable = false)
    private Address shippingAddress;


    public Order() {
        this.totalAmount = BigDecimal.ZERO;
        this.oderStatus =OderStatus.NEW ;
    }
    public void recalculateTotals() {
        this.totalAmount = OrderItem.stream()
                .map(orderItem ->
                        BigDecimal.valueOf(orderItem.getQuantity())
                                .multiply(BigDecimal.valueOf(orderItem.getUnitprice()))
                ).reduce(BigDecimal.ZERO,BigDecimal::add);}
    // Convenience constructor
    public Order(String orderNumber, BigDecimal totalAmount, OderStatus status, paymentMethod paymentMethod) {
        this.orderNumber = orderNumber;
        this.totalAmount = totalAmount == null ? BigDecimal.ZERO : totalAmount;
        this.oderStatus = status;
        this.paymentMethod = paymentMethod;
    }



    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderNumber='" + orderNumber + '\'' +
                ", totalAmount=" + totalAmount +
                ", createdAt=" + getCreatedAt() +
                ", status='" + oderStatus + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}