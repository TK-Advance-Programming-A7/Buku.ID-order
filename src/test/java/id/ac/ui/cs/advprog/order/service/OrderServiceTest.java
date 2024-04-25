package id.ac.ui.cs.advprog.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonIOException;
import id.ac.ui.cs.advprog.order.model.Order;
import id.ac.ui.cs.advprog.order.model.Book;
import id.ac.ui.cs.advprog.order.model.OrderItem;
import id.ac.ui.cs.advprog.order.repository.OrderItemRepository;
import id.ac.ui.cs.advprog.order.repository.OrderRepository;
import id.ac.ui.cs.advprog.order.service.OrderService;
import id.ac.ui.cs.advprog.order.status.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    private List<Order> orders;
    private ArrayList<OrderItem> orderItems;
    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        // Setup dummy books
        book1 = new Book(1, "Sampo Cap Bambang", "Bambang", "Bambang CV", 10.99f, 100, "1234567890", "sampo_cap_bambang.jpg", new Date(), "Children's Books", 50, "A children's book about Sampo Cap Bambang adventures.");
        book2 = new Book(2, "The Adventures of Sherlock Holmes", "Arthur Conan Doyle", "Penguin Classics", 8.50f, 75, "9780140439070", "sherlock_holmes.jpg", new Date(), "Mystery", 320, "A collection of twelve stories featuring Sherlock Holmes, a consulting detective.");

        // Setup dummy order items
        orderItems = new ArrayList<OrderItem>();
        OrderItem item1 = new OrderItem();
        item1.setIdBook(book1.getIdBook());
        item1.setAmount(2);
        item1.setPrice(book1.getPrice());

        OrderItem item2 = new OrderItem();
        item2.setIdBook(book2.getIdBook());
        item2.setAmount(1);
        item2.setPrice(book2.getPrice());

        // Add items to the order list
        orderItems.add(item1);
        orderItems.add(item2);

        orders = new ArrayList<>();

        Order order1 = new Order(888640678,
                orderItems,
                "Depok");
        orders.add(order1);
//        item1.setOrder(order1);
//        orderItemRepository.save(item1);
//        item2.setOrder(order1);
//        orderItemRepository.save(item2);
//        orderRepository.save(order1);
        Order order2 = new Order(888640679,
                orderItems,
                "Jakarta");
        orders.add(order2);
//        item1.setOrder(order2);
//        orderItemRepository.save(item1);
//        item2.setOrder(order2);
//        orderItemRepository.save(item2);
//        orderRepository.save(order2);
        Order order3 = new Order(888640680,
                orderItems,
                "Cibinong");
        orders.add(order3);
//        item1.setOrder(order3);
//        orderItemRepository.save(item1);
//        item2.setOrder(order3);
//        orderItemRepository.save(item2);
//        orderRepository.save(order3);
    }

    @Test
    void addBookToOrder_ValidBookId_BookAddedSuccessfully() throws JsonProcessingException {
        // Mock repository
        when(orderRepository.findById(anyInt())).thenReturn(Optional.of(orders.get(0)));

        // Test
        String result = orderService.addBookToOrder(orders.get(0).getIdOrder(), book1.getIdBook(), 1, book1.getPrice());

        assertNotNull(result);
        
        assertTrue(result.contains("\"amount\":3")); // Checking if the amount has been updated
    }

    @Test
    void decreaseBookInOrder_ValidBookId_BookDecreasedSuccessfully() throws JsonProcessingException {
        // Mock repository
        when(orderRepository.findById(anyInt())).thenReturn(Optional.of(orders.getFirst()));

        // Test
        String result = orderService.decreaseBookInOrder(orders.getFirst().getIdOrder(), book1.getIdBook(), 1);

        assertNotNull(result);
        
        assertTrue(result.contains("\"amount\":1")); // Checking if the amount has been updated
    }

    @Test
    void decreaseBookInOrder_InvalidBookId_ExceptionThrown() {
        // Mock repository
        when(orderRepository.findById(anyInt())).thenReturn(Optional.of(orders.get(0)));

        // Test
        assertThrows(NoSuchElementException.class, () -> {
            orderService.decreaseBookInOrder(orders.get(0).getIdOrder(), 9999, 1);
        });
    }

    @Test
    void updateNextStatus_ValidOrder_OrderStatusUpdatedSuccessfully() {
        // Mock order
        Order order = orders.get(0);
        order.setState(new WaitingCheckoutState()); // Initial state

        // Mock repository
        when(orderRepository.save(any())).thenReturn(order);

        // Test
        orderService.updateNextStatus(order);

        assertEquals("Waiting Payment", order.getState().toString()); // Expecting status to be updated to "Waiting Payment"
    }

    @Test
    void findAll_OrdersExist_ReturnsAllOrdersAsJson() throws JsonProcessingException {
        // Mock repository
        when(orderRepository.findAll()).thenReturn(orders);

        // Test
        String result = orderService.findAll();

        assertNotNull(result);
        
        assertTrue(result.contains("Depok")); // Expecting order with address "Depok" to be present in the JSON
    }

    @Test
    void getOrder_ValidOrderId_ReturnsOrderAsJson() throws JsonProcessingException {
        // Mock repository
        when(orderRepository.findById(anyInt())).thenReturn(Optional.of(orders.get(0)));

        // Test
        String result = orderService.getOrder(orders.get(0).getIdOrder());

        assertNotNull(result);
        assertTrue(result.contains("Depok")); // Expecting order with address "Depok" to be present in the JSON
    }

    @Test
    void getOrder_InvalidOrderId_ExceptionThrown() {
        // Mock repository
        when(orderRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Test
        assertThrows(NoSuchElementException.class, () -> {
            orderService.getOrder(9999); // Non-existing order ID
        });
    }

    @Test
    void addOrder_ValidOrder_OrderAddedSuccessfully() throws JsonProcessingException {
        // Mock repository
        when(orderRepository.save(any())).thenReturn(orders.get(0));

        // Test
        String result = orderService.addOrder(orders.get(0));

        assertNotNull(result);
        
        assertTrue(result.contains("Depok")); // Expecting order with address "Depok" to be present in the JSON
    }

    @Test
    void getOrderStatus_ValidOrderId_ReturnsOrderStatus() {
        // Mock repository
        when(orderRepository.findById(anyInt())).thenReturn(Optional.of(orders.get(0)));

        // Test
        String result = orderService.getOrderState(orders.get(0).getIdOrder());

        assertNotNull(result);
        assertEquals("\"Waiting Checkout\"", result); // Expecting status to be "WaitingCheckout"
    }

    @Test
    void getOrderStatus_InvalidOrderId_ExceptionThrown() {
        // Mock repository
        when(orderRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Test
        assertThrows(NoSuchElementException.class, () -> {
            orderService.getOrderState(9999); // Non-existing order ID
        });
    }

    @Test
    void cancelOrder_ValidOrderId_OrderCancelledSuccessfully() throws JsonProcessingException {
        // Mock repository
        when(orderRepository.findById(anyInt())).thenReturn(Optional.of(orders.get(0)));
        when(orderRepository.save(any())).thenReturn(orders.get(0));

        // Test
        String result = orderService.cancelOrder(orders.get(0).getIdOrder());

        assertNotNull(result);
        
        assertEquals("Cancelled", orders.get(0).getState().toString()); // Expecting status to be "Cancelled"
    }

    @Test
    void cancelOrder_InvalidOrderId_ExceptionThrown() {
        // Mock repository
        when(orderRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Test
        assertThrows(NoSuchElementException.class, () -> {
            orderService.cancelOrder(9999); // Non-existing order ID
        });
    }

}
