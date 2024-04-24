package id.ac.ui.cs.advprog.order.model;

import id.ac.ui.cs.advprog.order.status.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "Orders")
public class Order {

    @Getter @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_order")
    private int idOrder;

    @Getter @Setter
    @Column(name = "id_user")
    private int idUser;

    @Getter @Setter
    @Column(name = "order_date")
    private Date orderDate;

    @Getter
    @Column(name = "address")
    private String address;

    @Getter @Setter
    @OneToMany(mappedBy = "order")
    private Map<Integer, OrderItem> items = new HashMap<>();

    @Getter
    @Column(name = "total_price")
    private float totalPrice;

    @Setter
    @Column(name = "cancelable")
    private boolean cancelable;

    @Getter
    @Column(name = "status")
    private String status;

    @Getter
    @Transient
    private State state;

    public Order() {

    }

    public Order(int idUser) {
        this.idUser = idUser;
        this.orderDate = new Date();
        setState(new WaitingCheckoutState());
        this.setTotalPrice();
    }

    public Order(int idUser, Map<Integer, OrderItem> newItems, String address) {
        this.idUser = idUser;
        this.orderDate = new Date();
        setState(new WaitingCheckoutState());
        this.items = newItems;
        this.address = address;
        this.setTotalPrice();
    }

    public void addBook(int bookId, int quantity, float price) {
        OrderItem item = items.get(bookId);
        if (item != null) {
            item.setAmount(item.getAmount() + quantity);
        } else {
            item = new OrderItem();
            item.setIdBook(bookId);
            item.setAmount(quantity);
            item.setPrice(price);
            items.put(bookId, item);
        }
        setTotalPrice();
    }

    public void decreaseBook(int bookId, int quantity) {
        OrderItem item = items.get(bookId);
        if (item != null) {
            int newQuantity = item.getAmount() - quantity;
            if (newQuantity <= 0) {
                items.remove(bookId);
            } else {
                item.setAmount(newQuantity);
            }
            setTotalPrice();
        } else {
            throw new NoSuchElementException("Book with ID " + bookId + " not found in the order.");
        }
    }



    public void setTotalPrice() {
        float total = 0;
        for (OrderItem item : items.values()) {
            total += item.getTotalPrice();
        }
        this.totalPrice = total;
    }

    public void setStatus(String status) {
        if ("Waiting Checkout".equals(status)) {
            if (!(this.state instanceof WaitingCheckoutState)) {
                this.state = new WaitingCheckoutState();
            }
        } else if ("Waiting Payment".equals(status)) {
            if (!(this.state instanceof WaitingPaymentState)) {
                this.state = new WaitingPaymentState();
            }
        } else if ("Cancelled".equals(status)) {
            if (!(this.state instanceof CancelledState)) {
                this.state = new CancelledState();
            }
        } else if ("Waiting Delivered".equals(status)) {
            if (!(this.state instanceof WaitingDeliveredState)) {
                this.state = new WaitingDeliveredState();
            }
        } else {
            throw new IllegalArgumentException("Invalid state value: " + status);
        }
        this.status = this.state.toString();
    }


    public void setState(State state) {
        this.setState(state);
        this.setStatus(state.toString());
        this.cancelable = state.isCancelable();
    }

    public void nextStatus(){
        state.nextState(this);
        setState(state);
    }

    public void cancelOrder(){
        if (cancelable){
            setState(new CancelledState());
        }
    }

    public boolean isCancelable() {
        return this.cancelable;
    }

    public void setAddress(String address) {
        if (address == null || address.isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty");
        }
        this.address = address;
    }
}