package id.ac.ui.cs.advprog.order.status;

import id.ac.ui.cs.advprog.order.model.Order;
public class CancelledState implements State {
    @Override
    public String toString() {
        return "Cancelled";
    }

    @Override
    public void nextState(Order order) {
    }

    @Override
    public boolean isCancelable() {
        return false;
    }
}
