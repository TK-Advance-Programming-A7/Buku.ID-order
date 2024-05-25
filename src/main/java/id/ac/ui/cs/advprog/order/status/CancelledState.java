package id.ac.ui.cs.advprog.order.status;

import id.ac.ui.cs.advprog.order.model.Order;
public class CancelledState implements State {
    @Override
    public String toString() {
        return "Cancelled";
    }

    @Override
    public void nextState(Order order) {
        // Currently no transition to the next state from Cancelled state
    }

    @Override
    public boolean isCancelable() {
        return false;
    }
}
