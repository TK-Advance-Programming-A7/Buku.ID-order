package id.ac.ui.cs.advprog.order.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;


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

    @ManyToOne
    @JoinColumn(name = "id_order")
    private Order order;

}