package id.ac.ui.cs.advprog.order.status;

import id.ac.ui.cs.advprog.order.model.Order;

public interface State {

    public String toString();

    public void nextState(Order order);

    public boolean isCancelable();
}
