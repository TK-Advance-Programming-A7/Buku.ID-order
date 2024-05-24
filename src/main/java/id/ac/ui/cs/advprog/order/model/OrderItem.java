package id.ac.ui.cs.advprog.order.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import id.ac.ui.cs.advprog.order.repository.OrderItemRepository;
import id.ac.ui.cs.advprog.order.status.WaitingCheckoutState;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import lombok.ToString;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.Date;


@Getter @Setter
@Entity
public abstract class OrderItem implements OrderItemRepository {

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