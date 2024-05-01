package id.ac.ui.cs.advprog.order.model;

import com.fasterxml.jackson.annotation.*;
import id.ac.ui.cs.advprog.order.status.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.*;

import jakarta.persistence.*;

import java.text.SimpleDateFormat;
import java.util.*;

@Entity
@Table(name = "Orders")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "idOrder")
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
    private String orderDate;


    @Getter
    @Column(name = "address")
    private String address;

    @Getter @Setter @ToString.Exclude @JsonManagedReference @EqualsAndHashCode.Exclude @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();

    @Getter @Setter
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
        this.orderDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());
        setState(new WaitingCheckoutState());
        this.setTotalPrice();
    }

    public Order(int idUser, ArrayList<OrderItem> newItems, String address) {
        this.idUser = idUser;
        this.orderDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());
        setState(new WaitingCheckoutState());
        this.items = newItems;
        for(OrderItem item: items){
            item.setOrder(this);
        }
        this.address = address;
        this.setTotalPrice();
    }

    public void setTotalPrice() {
        float total = 0;
        for (OrderItem item : items) {
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
        this.state = state;
        this.setStatus(state.toString());
        this.cancelable = state.isCancelable();
    }

    public void nextStatus(){
        this.setStatus(this.status);
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