package id.ac.ui.cs.advprog.order.model;

import id.ac.ui.cs.advprog.order.status.CancelledState;
import id.ac.ui.cs.advprog.order.status.State;
import id.ac.ui.cs.advprog.order.status.WaitingCheckoutState;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "Orders")
public class Order {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_order")
    private int idOrder;

    @Getter
    @Column(name = "id_user")
    private int idUser;

    @Getter
    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Getter
    @Column(name = "status")
    private String status;

    @Getter 
    @Column(name = "address")
    private String address;

    @Getter
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Map<Integer, OrderItem> items = new HashMap<>();

    @Getter
    @Column(name = "total_price")
    private float totalPrice;

    @Setter
    @Column(name = "cancelable")
    private boolean cancelable;

    @Getter
    @Transient
    private State state;

    public Order() {

    }

    public Order(int idUser) {
        this.idUser = idUser;
        this.orderDate = LocalDateTime.now();
        setState(new WaitingCheckoutState());
    }

    public Order(int idUser, Map<Integer, OrderItem> newItems, String address) {
        this.idUser = idUser;
        this.orderDate = LocalDateTime.now(); 
        setState(new WaitingCheckoutState());
        this.items = newItems;
        this.address = address;
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

    public void setStatus(State state){
        this.state = state;
        this.status = state.toString();
    }
    
    public void setState(State state) {
        this.setStatus(state); 
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