package id.ac.ui.cs.advprog.order.model;

import id.ac.ui.cs.advprog.order.status.CancelledState;
import id.ac.ui.cs.advprog.order.status.State;
import id.ac.ui.cs.advprog.order.status.WaitingCheckoutState;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private List<OrderItem> items = new ArrayList<>();

    @Getter
    @Column(name = "total_price")
    private float totalPrice;

    @Setter
    @Column(name = "cancelable")
    private boolean cancelable;

    @Getter
    @Transient
    private State state;

    public Order(int idUser) {
        this.idUser = idUser;
        this.orderDate = LocalDateTime.now();
        setState(new WaitingCheckoutState());
    }

    public Order(int idUser, ArrayList<OrderItem> newItems) {
        this.idUser = idUser;
        this.orderDate = LocalDateTime.now(); 
        setState(new WaitingCheckoutState());
        this.items = newItems;
    }

    public void setTotalPrice() {
        float total = 0;
        for (OrderItem item : items) {
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
        cancelable = state.isCancelable();
        status = state.toString();
    }

    public void cancelOrder(){

        if (cancelable){
            state = new CancelledState();
        }
        cancelable = state.isCancelable();
        status = state.toString();
    }

    public boolean getCancelable() {
        return this.cancelable;
    }

    public void setAddress(String address) {
        if (address == null || address.isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty");
        }
        this.address = address;
    }
}