package id.ac.ui.cs.advprog.order.model;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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

    }

    public void nextStatus(){

    }

    public void cancelOrder(){

    }

}
