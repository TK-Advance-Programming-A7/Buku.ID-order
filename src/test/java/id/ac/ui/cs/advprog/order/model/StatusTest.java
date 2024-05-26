package id.ac.ui.cs.advprog.order.model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StatusTest {

    @Test
    void testAllStatusStateAndCancelable() {
        Order order = new Order("1");
        assertEquals("Waiting Checkout", order.getStatus());
        assertTrue(order.isCancelable());

        order.nextStatus();
        assertEquals("Waiting Payment", order.getStatus());
        assertTrue(order.isCancelable());

        order.nextStatus();
        assertEquals("Waiting Delivered", order.getStatus());
        assertFalse(order.isCancelable());


        order = new Order("1");
        order.cancelOrder();
        assertEquals("Cancelled", order.getStatus());
        order.nextStatus();
        assertEquals("Cancelled", order.getStatus());
        assertFalse(order.isCancelable());
    }
}
