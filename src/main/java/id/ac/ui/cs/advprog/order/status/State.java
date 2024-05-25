package id.ac.ui.cs.advprog.order.status;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import id.ac.ui.cs.advprog.order.model.Order;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = WaitingCheckoutState.class, name = "WaitingCheckout"),
        @JsonSubTypes.Type(value = WaitingPaymentState.class, name = "WaitingPayment"),
        @JsonSubTypes.Type(value = WaitingDeliveredState.class, name = "WaitingDelivered"),
        @JsonSubTypes.Type(value = CancelledState.class, name = "Cancelled")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public interface State {

    public String toString();

    public void nextState(Order order);

    public boolean isCancelable();
}
