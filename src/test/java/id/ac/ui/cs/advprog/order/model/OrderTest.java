package id.ac.ui.cs.advprog.order.model;

import id.ac.ui.cs.advprog.order.status.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {
    private Order order;
    private ArrayList<OrderItem> orderItems;

    @BeforeEach
    void setUp() {

        // Setup dummy order items
        orderItems = new ArrayList<>();
        OrderItem item1 = new OrderItem();
        item1.setIdBook(1);
        item1.setAmount(2);
        item1.setPrice(10.99f);

        OrderItem item2 = new OrderItem();
        item2.setIdBook(2);
        item2.setAmount(1);
        item2.setPrice(8.50f);

        // Add items to the order list
        orderItems.add(item1);
        orderItems.add(item2);

        // Initialize the order
        order = new Order("888640678");
        order.getItems().addAll(orderItems);
        order.setTotalPrice();
    }

    @Test
    void testOrderCreationWithIdUser() {
        Order newOrder = new Order("123456789");
        assertEquals("123456789", newOrder.getIdUser());
        assertNotNull(newOrder.getOrderDate());
        assertInstanceOf(WaitingCheckoutState.class, newOrder.getState());
    }

    @Test
    void testOrderCreationWithIdUserAndItems() {
        ArrayList<OrderItem> newItems = new ArrayList<>(orderItems);
        Order newOrder = new Order("123456789", newItems, "UI");
        assertEquals("123456789", newOrder.getIdUser());
        assertNotNull(newOrder.getOrderDate());
        assertInstanceOf(WaitingCheckoutState.class, newOrder.getState());
        assertEquals(newItems, newOrder.getItems());
    }

    @Test
    void testSetCancelable() {
        order.setCancelable(true);
        assertTrue(order.isCancelable());
    }

    @Test
    void testSetState() {
        order.setState(new CancelledState());
        assertInstanceOf(CancelledState.class, order.getState());
    }

    @Test
    void testSetStateCheckout() {
        order.setState(new WaitingCheckoutState());
        assertInstanceOf(WaitingCheckoutState.class, order.getState());
    }

    @Test
    void testSetStatusCheckout() {
        order.setStatus("Waiting Checkout");
        assertInstanceOf(WaitingCheckoutState.class, order.getState());
        assertEquals("Waiting Checkout", order.getStatus());
    }

    @Test
    void testSetStatusWaitingCheckoutSameState() {
        order.setState(new WaitingCheckoutState());
        order.setStatus("Waiting Checkout");
        assertInstanceOf(WaitingCheckoutState.class, order.getState());
        assertEquals("Waiting Checkout", order.getStatus());
    }

    @Test
    void testSetStatusWaitingDelivered() {
        order.setStatus("Waiting Delivered");
        assertEquals("Waiting Delivered", order.getStatus());
        assertInstanceOf(WaitingDeliveredState.class, order.getState());
    }

    @Test
    void testSetAddress() {
        String address = "123 Test Street";
        order.setAddress(address);
        assertEquals(address, order.getAddress());
    }

    @Test
    void testSetAddressWithNull() {
        assertThrows(IllegalArgumentException.class, () -> order.setAddress(null));
    }

    @Test
    void testSetAddressWithEmptyString() {
        assertThrows(IllegalArgumentException.class, () -> order.setAddress(""));
    }

    @Test
    void testSetStatusWaitingCheckout() {
        order.setStatus("Waiting Checkout");
        assertEquals("Waiting Checkout", order.getStatus());
    }

    @Test
    void testSetStatusWaitingPayment() {
        order.setStatus("Waiting Payment");
        assertEquals("Waiting Payment", order.getStatus());
    }

    @Test
    void testSetStatusCancelled() {
        order.setStatus("Cancelled");
        assertEquals("Cancelled", order.getStatus());
    }

    @Test
    void testSetStatusInvalid() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            order.setStatus("Invalid Status");
        });

        String expectedMessage = "Invalid state value: Invalid Status";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testCancelOrderWhenCancelable() {
        order.setCancelable(true);
        order.cancelOrder();
        assertEquals("Cancelled", order.getStatus());
    }

    @Test
    void testCancelOrderWhenNotCancelable() {
        order.setCancelable(false);
        order.cancelOrder();
        assertNotEquals("Cancelled", order.getStatus());
    }

    @Test
    void testSetAndGetOrder() {
        Order testOrder = new Order();
        testOrder.setIdOrder(1);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(testOrder);

        assertEquals(testOrder, orderItem.getOrder());
    }

    @Test
    void testSetStatus() {
        Order order = new Order();
        order.setStatus("Waiting Checkout");
        assertEquals("Waiting Checkout", order.getStatus());

        order.setStatus("Waiting Payment");
        assertEquals("Waiting Payment", order.getStatus());

        order.setStatus("Cancelled");
        assertEquals("Cancelled", order.getStatus());

        order.setStatus("Waiting Delivered");
        assertEquals("Waiting Delivered", order.getStatus());
    }

    @Test
    void testSetIdUser() {
        Order order = new Order();
        order.setIdUser("12345");
        assertEquals("12345", order.getIdUser());
    }

    @Test
    void testSetOrderDate() {
        Order order = new Order();
        String date = "2024-05-26T14:30:00";
        order.setOrderDate(date);
        assertEquals(date, order.getOrderDate());
    }

    @Test
    void testSetItems() {
        Order order = new Order();
        List<OrderItem> items = new ArrayList<>();
        OrderItem item1 = new OrderItem();
        item1.setIdBook(1);
        item1.setAmount(2);
        item1.setPrice(10.99f);

        OrderItem item2 = new OrderItem();
        item2.setIdBook(2);
        item2.setAmount(1);
        item2.setPrice(8.50f);

        items.add(item1);
        items.add(item2);

        order.setItems(items);
        assertEquals(items, order.getItems());
    }

    @Test
    void testSetTotalPrice() {
        Order order = new Order();
        float totalPrice = 50.0f;
        order.setTotalPrice(totalPrice);
        assertEquals(totalPrice, order.getTotalPrice(), 0.001);
    }
}