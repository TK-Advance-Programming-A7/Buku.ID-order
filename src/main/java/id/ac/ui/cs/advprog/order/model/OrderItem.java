package id.ac.ui.cs.advprog.order.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import lombok.ToString;


@Getter @Setter
@Entity
public class OrderItem {

    @Column(name = "id_book")
    private int idBook;

    @Column(name = "amount")
    private int amount;

    @Column(name = "price")
    private float price;

    public float getTotalPrice() {
        return price * amount;
    }

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orderItem")
    private int idOrderItem;

    @Setter @Getter @JsonBackReference @EqualsAndHashCode.Exclude
    @ManyToOne @ToString.Exclude
    @JoinColumn(name = "id_order")
    private Order order;

    public OrderItem() {
    }
}