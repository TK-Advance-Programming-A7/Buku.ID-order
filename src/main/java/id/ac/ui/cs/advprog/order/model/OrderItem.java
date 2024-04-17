package id.ac.ui.cs.advprog.order.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter @Setter
public class OrderItem {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_book")
    private Book book;

    @Column(name = "amount")
    private int amount;

    @Column(name = "price")
    private float price;

    public float getTotalPrice() {
        return price * amount;
    }

}