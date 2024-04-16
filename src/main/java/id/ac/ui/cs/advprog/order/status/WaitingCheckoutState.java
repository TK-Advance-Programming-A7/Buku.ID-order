package id.ac.ui.cs.advprog.order.status;

import id.ac.ui.cs.advprog.order.model.Order;
public class WaitingCheckoutState implements State {
    @Override
    public String toString() {
        return "Waiting Checkout";
    }

    @Override
    public void nextState(Order order) {
        order.setState(new WaitingPaymentState());
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

}
