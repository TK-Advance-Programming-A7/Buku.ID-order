package id.ac.ui.cs.advprog.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.ac.ui.cs.advprog.order.model.Order;
import id.ac.ui.cs.advprog.order.model.OrderItem;
import id.ac.ui.cs.advprog.order.repository.OrderItemRepository;
import id.ac.ui.cs.advprog.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    private List<Order> orders;
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

        orders = new ArrayList<>();

        Order order1 = new Order(888640678,
                orderItems,
                "Depok");
        orders.add(order1);
        Order order2 = new Order(888640679,
                orderItems,
                "Jakarta");
        orders.add(order2);
        Order order3 = new Order(888640680,
                orderItems,
                "Cibinong");
        orders.add(order3);
    }

    @Test
    void addBookToOrder_ValidBookId_BookAddedSuccessfully() throws JsonProcessingException {
        // Mock repository
        when(orderRepository.findById(anyInt())).thenReturn(Optional.of(orders.getFirst()));

        // Test
        String result = orderService.addBookToOrder(orders.getFirst().getIdOrder(), 1, 1, 10.99f);

        assertNotNull(result);
        
        assertTrue(result.contains("\"amount\":3")); // Checking if the amount has been updated
    }

    @Test
    void decreaseBookInOrder_ValidBookId_BookDecreasedSuccessfully() throws JsonProcessingException {
        // Mock repository
        when(orderRepository.findById(anyInt())).thenReturn(Optional.of(orders.getFirst()));

        // Test
        String result = orderService.decreaseBookInOrder(orders.getFirst().getIdOrder(), 1, 1);

        assertNotNull(result);
        
        assertTrue(result.contains("\"amount\":1")); // Checking if the amount has been updated
    }


    void decreaseBookInOrder_InvalidBookId_ExceptionThrown() {
        // Mock repository
        when(orderRepository.findById(anyInt())).thenReturn(Optional.of(orders.getFirst()));

        // Test
        int invalidBookId = 9999;
        assertThrows(NoSuchElementException.class, () -> {
            if (!orders.isEmpty()) {
                orderService.decreaseBookInOrder(orders.getFirst().getIdOrder(), invalidBookId, 1);
            }
        });
    }


    @Test
    void updateNextStatus_ValidOrder_OrderStatusUpdatedSuccessfully() throws JsonProcessingException {
        when(orderRepository.findById(orders.getFirst().getIdOrder())).thenReturn(Optional.of(orders.getFirst()));
        when(orderRepository.save(any(Order.class))).thenReturn(orders.getFirst());
        orderService.updateNextStatus(orders.getFirst().getIdOrder());
        assertEquals("Waiting Payment", orders.getFirst().getState().toString()); // Expecting status to be updated to "Waiting Payment"
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
        when(orderRepository.findById(anyInt())).thenReturn(Optional.of(orders.getFirst()));

        // Test
        String result = orderService.getOrder(orders.getFirst().getIdOrder());

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
        when(orderRepository.save(any())).thenReturn(orders.getFirst());

        // Test
        String result = orderService.addOrder(orders.getFirst());

        assertNotNull(result);
        
        assertTrue(result.contains("Depok")); // Expecting order with address "Depok" to be present in the JSON
    }

    @Test
    void getOrderStatus_ValidOrderId_ReturnsOrderStatus() {
        // Mock repository
        when(orderRepository.findById(anyInt())).thenReturn(Optional.of(orders.getFirst()));

        // Test
        String result = orderService.getOrderState(orders.getFirst().getIdOrder());

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
        when(orderRepository.findById(anyInt())).thenReturn(Optional.of(orders.getFirst()));
        when(orderRepository.save(any())).thenReturn(orders.getFirst());

        // Test
        String result = orderService.cancelOrder(orders.getFirst().getIdOrder());

        assertNotNull(result);
        
        assertEquals("Cancelled", orders.getFirst().getState().toString()); // Expecting status to be "Cancelled"
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
    
    @Test
    void deleteItemFromOrder_ExistingItem_Success() throws JsonProcessingException {
 
        Order order = new Order();
        order.setIdOrder(1);
        OrderItem item = new OrderItem();
        item.setIdOrderItem(1);
        order.getItems().add(item);
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(orderItemRepository.findById(1)).thenReturn(Optional.of(item));

        String result = orderService.deleteItemFromOrder(1, 1);

        verify(orderRepository, times(2)).save(order);
        verify(orderItemRepository, times(1)).deleteById(1); 
        assertNotNull(result);
    }

    @Test
    void deleteItemFromOrder_NonExistingItem() {
        lenient().when(orderItemRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> orderService.deleteItemFromOrder(1, 999));
    }

    @Test
    void getOrdersByUserIdAndStatus_ValidUserIdAndStatus_OrdersRetrievedSuccessfully() throws JsonProcessingException {
        int userId = 123;
        String status = "Waiting Checkout";
        when(orderRepository.findAllByIdUserAndStatus(userId, status)).thenReturn(orders);

        String result = orderService.getOrdersByUserIdAndStatus(userId, status);

        assertNotNull(result);
        verify(orderRepository, times(1)).findAllByIdUserAndStatus(userId, status);
    }

}
