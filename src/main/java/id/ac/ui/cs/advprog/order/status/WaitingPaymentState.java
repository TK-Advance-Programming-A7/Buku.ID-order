package id.ac.ui.cs.advprog.order.status;

import id.ac.ui.cs.advprog.order.model.Order;
public class WaitingPaymentState implements State{
    @Override
    public String toString() {
        return "Waiting Payment";
    }

    @Override
    public void nextState(Order order) {
        order.setState(new WaitingDeliveredState());
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
