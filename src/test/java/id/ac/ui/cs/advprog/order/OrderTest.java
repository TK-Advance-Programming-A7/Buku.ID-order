package id.ac.ui.cs.advprog.order;

import id.ac.ui.cs.advprog.order.model.Book;
import id.ac.ui.cs.advprog.order.model.Order;
import id.ac.ui.cs.advprog.order.model.OrderItem;
import id.ac.ui.cs.advprog.order.status.CancelledState;
import id.ac.ui.cs.advprog.order.status.WaitingCheckoutState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {
    private Order order;
    private List<OrderItem> orderItems;
    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        // Setup dummy books
        book1 = new Book(1, "Sampo Cap Bambang", "Bambang", "Bambang CV", 10.99f, 100, "1234567890", "sampo_cap_bambang.jpg", new Date(), "Children's Books", 50, "A children's book about Sampo Cap Bambang adventures.");
        book2 = new Book(2, "The Adventures of Sherlock Holmes", "Arthur Conan Doyle", "Penguin Classics", 8.50f, 75, "9780140439070", "sherlock_holmes.jpg", new Date(), "Mystery", 320, "A collection of twelve stories featuring Sherlock Holmes, a consulting detective.");

        // Setup dummy order items
        orderItems = new ArrayList<>();
        OrderItem item1 = new OrderItem();
        item1.setBook(book1);
        item1.setAmount(2);
        item1.setPrice(book1.getPrice());

        OrderItem item2 = new OrderItem();
        item2.setBook(book2);
        item2.setAmount(1);
        item2.setPrice(book2.getPrice());

        // Add items to the order list
        orderItems.add(item1);
        orderItems.add(item2);

        // Initialize the order
        order = new Order(888640678);
        order.getItems().addAll(orderItems);
        order.setTotalPrice();
    }

    @Test
    void testOrderCreationWithIdUser() {
        Order newOrder = new Order(123456789);
        assertEquals(123456789, newOrder.getIdUser());
        assertNotNull(newOrder.getOrderDate());
        assertTrue(newOrder.getState() instanceof WaitingCheckoutState);
    }

    @Test
    void testOrderCreationWithIdUserAndItems() {
        ArrayList<OrderItem> newItems = new ArrayList<>(orderItems);
        Order newOrder = new Order(123456789, newItems);
        assertEquals(123456789, newOrder.getIdUser());
        assertNotNull(newOrder.getOrderDate());
        assertTrue(newOrder.getState() instanceof WaitingCheckoutState);
        assertEquals(newItems, newOrder.getItems());
    }

    @Test
    void testSetCancelable() {
        order.setCancelable(true);
        assertTrue(order.getCancelable());
    }

    @Test
    void testSetState() {
        order.setState(new CancelledState());
        assertTrue(order.getState() instanceof CancelledState);
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

}