package id.ac.ui.cs.advprog.order.model;
import id.ac.ui.cs.advprog.order.status.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Order {
    @Getter @Setter
    @Transient
    private State state;

    @Getter @Setter
    @Column(name = "status")
    private String status;

    @Getter @Setter
    @Transient
    private Boolean cancelable;

    public Order(int amount) {
        state = new WaitingCheckoutState();
        cancelable = state.isCancelable();
        status = state.toString();
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

}
