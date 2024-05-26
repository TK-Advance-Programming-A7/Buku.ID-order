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
import id.ac.ui.cs.advprog.order.status.*;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    private final ObjectMapper objectMapper = new ObjectMapper();

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

        Order order1 = new Order("888640678",
                orderItems,
                "Depok");
        orders.add(order1);
        Order order2 = new Order("888640679",
                orderItems,
                "Jakarta");
        orders.add(order2);
        Order order3 = new Order("888640680",
                orderItems,
                "Cibinong");
        orders.add(order3);
    }


    @Test
    void deleteItemFromOrder_NonExistingItem_ExceptionThrown2() {
        // Adding an order to the repository first
        Order order = orders.getFirst();
        when(orderRepository.findById(order.getIdOrder())).thenReturn(Optional.of(order));

        int orderId = order.getIdOrder();
        int itemId = 999; // Non-existing item ID

        // Mocking the repository behavior
        when(orderItemRepository.findById(itemId)).thenReturn(Optional.empty());

        // Performing the test
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            orderService.deleteItemFromOrder(orderId, itemId);
        });

        // Verifying the exception message
        assertEquals("Order Item with ID " + itemId + " not found", exception.getMessage());
    }

    @Test
    void decreaseBookInOrder_BookQuantityDecreasedToZero_ItemRemovedSuccessfully() throws JsonProcessingException {
        // Given
        int orderId = orders.get(0).getIdOrder();
        int bookId = 1;
        int quantity = 2; // Setting quantity to remove to 2, which matches the current amount of bookId 1 in the order

        Order order = orders.get(0);
        OrderItem itemToRemove = order.getItems().stream().filter(item -> item.getIdBook() == bookId).findFirst().orElse(null);

        assertNotNull(itemToRemove, "Precondition failed: The item to be removed should exist in the order");

        // Mock repository
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // When
        String result = orderService.decreaseBookInOrder(orderId, bookId, quantity);

        // Then
        assertNotNull(result);
        verify(orderRepository, times(2)).save(order); // Once for the removal and once at the end of the method
        verify(orderItemRepository, times(1)).deleteById(itemToRemove.getIdOrderItem());
        assertFalse(order.getItems().contains(itemToRemove), "The item should be removed from the order");
        assertTrue(result.contains("\"items\":[]"), "The resulting JSON should indicate that no items are left in the order");
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

    @Test
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
        String userId = "123";
        String status = "Waiting Checkout";
        when(orderRepository.findAllByIdUserAndStatus(userId, status)).thenReturn(orders);

        String result = orderService.getOrdersByUserIdAndStatus(userId, status);

        assertNotNull(result);
        verify(orderRepository, times(1)).findAllByIdUserAndStatus(userId, status);
    }

    @Test
    void editOrder_ValidOrderIdAndOrder_OrderEditedSuccessfully() throws JsonProcessingException {
        // Mock repository
        Order existingOrder = new Order();
        existingOrder.setIdOrder(1);
        when(orderRepository.findById(anyInt())).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(existingOrder);

        Order updatedOrder = new Order();
        updatedOrder.setIdOrder(1);
        updatedOrder.setStatus("Waiting Checkout");
        updatedOrder.setAddress("Updated Address");

        // Test
        String result = orderService.editOrder(1, updatedOrder);

        assertNotNull(result);
        assertEquals("Waiting Checkout", existingOrder.getStatus());
        assertEquals("Updated Address", existingOrder.getAddress());
    }

    @Test
    void deleteOrder_ValidOrderId_OrderDeletedSuccessfully() {
        // Mock repository
        Order existingOrder = new Order();
        existingOrder.setIdOrder(1);
        when(orderRepository.findById(anyInt())).thenReturn(Optional.of(existingOrder));

        // Test
        String result = orderService.deleteOrder(1);

        assertNotNull(result);
        verify(orderRepository, times(1)).delete(existingOrder);
    }

    @Test
    void deleteItemFromOrder_ExistingItem_ItemDeletedSuccessfully() throws JsonProcessingException {
        // Mock repository
        Order order = new Order();
        order.setIdOrder(1);
        OrderItem item = new OrderItem();
        item.setIdOrderItem(1);
        order.getItems().add(item);
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(orderItemRepository.findById(1)).thenReturn(Optional.of(item));

        // Test
        String result = orderService.deleteItemFromOrder(1, 1);

        verify(orderRepository, times(2)).save(order);
        verify(orderItemRepository, times(1)).deleteById(1);
        assertNotNull(result);
    }

    @Test
    void deleteItemFromOrder_NonExistingItem_ExceptionThrown() {
        // Mock repository
        lenient().when(orderItemRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Test
        assertThrows(NoSuchElementException.class, () -> orderService.deleteItemFromOrder(1, 999));
    }

    @Test
    public void testFindOrderItemById() {
        int orderItemId = 1;
        OrderItem orderItem = new OrderItem();
        orderItem.setIdOrderItem(orderItemId);

        when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.of(orderItem));

        OrderItem foundOrderItem = orderService.findOrderItemById(orderItemId);

        assertNotNull(foundOrderItem);
        assertEquals(orderItemId, foundOrderItem.getIdOrderItem());
    }

    @Test
    public void testFindOrderItemByIdThrowsException() {
        int orderItemId = 1;

        when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> orderService.findOrderItemById(orderItemId));

        assertEquals("Order Item with ID " + orderItemId + " not found", exception.getMessage());
    }

    @Test
    public void testGetOrderItemsByOrder() {
        int orderId = 1;
        Order order = new Order();
        order.setIdOrder(orderId);

        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(new OrderItem());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrder(order)).thenReturn(orderItems);

        List<OrderItem> foundOrderItems = orderService.getOrderItemsByOrder(orderId);

        assertNotNull(foundOrderItems);
        assertEquals(1, foundOrderItems.size());
    }

    @Test
    public void testGetOrderItemsByOrderThrowsException() {
        int orderId = 1;

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> orderService.getOrderItemsByOrder(orderId));

        assertEquals("Order with ID " + orderId + " not found", exception.getMessage());
    }

    @Test
    public void testGetAllOrdersOfUser() throws JsonProcessingException {
        String userId = "user123";
        List<Order> orders = new ArrayList<>();
        Order order = new Order();
        order.setStatus("Waiting Checkout"); // Initialize status
        orders.add(order);


        when(orderRepository.findAllByIdUser(userId)).thenReturn(orders);

        String ordersJson = orderService.getAllOrdersOfUser(userId);

        assertNotNull(ordersJson);
        verify(orderRepository, times(1)).findAllByIdUser(userId);
    }

    @Test
    public void testFindOrderItemByBookId() {
        Order order = new Order();
        OrderItem orderItem = new OrderItem();
        int bookId = 1;
        orderItem.setIdBook(bookId);
        order.setItems(List.of(orderItem));

        OrderItem foundOrderItem = orderService.findOrderItemByBookId(order, bookId);

        assertNotNull(foundOrderItem);
        assertEquals(bookId, foundOrderItem.getIdBook());
    }

    @Test
    public void testFindOrderItemByBookIdReturnsNull() {
        Order order = new Order();
        int bookId = 1;

        OrderItem foundOrderItem = orderService.findOrderItemByBookId(order, bookId);

        assertNull(foundOrderItem);
    }

    @Test
    public void testAddBookToOrder() throws JsonProcessingException {
        int orderId = 1;
        int bookId = 1;
        int quantity = 1;
        float price = 10.0f;
        Order order = new Order();
        order.setIdOrder(orderId);
        order.setStatus("Waiting Checkout"); // Initialize status

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String orderJson = orderService.addBookToOrder(orderId, bookId, quantity, price);

        assertNotNull(orderJson);
        verify(orderRepository, times(1)).save(order);
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }


    @Test
    public void testDecreaseBookInOrder() throws JsonProcessingException {
        int orderId = 1;
        int bookId = 1;
        int quantity = 1;
        Order order = new Order();
        OrderItem orderItem = new OrderItem();
        orderItem.setIdBook(bookId);
        orderItem.setAmount(2);
        order.setItems(List.of(orderItem));
        order.setStatus("Waiting Checkout"); // Initialize status

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        String orderJson = orderService.decreaseBookInOrder(orderId, bookId, quantity);

        assertNotNull(orderJson);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    public void testDecreaseBookInOrderThrowsException() {
        int orderId = 1;
        int bookId = 1;
        int quantity = 1;
        Order order = new Order();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> orderService.decreaseBookInOrder(orderId, bookId, quantity));

        assertEquals("Book with ID " + bookId + " not found in the order.", exception.getMessage());
    }

    @Test
    public void testCancelOrder() throws JsonProcessingException {
        orders.getFirst().setStatus("Cancelled");

        // Mocking the repository and service methods
        when(orderRepository.findById(orders.getFirst().getIdOrder())).thenReturn(Optional.of(orders.getFirst()));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Calling the method under test
        String orderJson = orderService.cancelOrder(orders.getFirst().getIdOrder());

        // Asserting and verifying the behavior
        assertNotNull(orderJson);
        verify(orderRepository, times(1)).save(orders.getFirst());
    }

    @Test
    public void testCancelOrderThrowsException() {
        int orderId = 1;
        Order order = new Order();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> orderService.cancelOrder(orderId));

        assertEquals("Order with ID " + orderId + " not found", exception.getMessage());
    }

    @Test
    public void testDeleteItemFromOrder() throws JsonProcessingException {
        int orderId = 1;
        int itemId = 1;
        Order order = new Order();
        order.setStatus("Waiting Checkout"); // Initialize status
        OrderItem orderItem = new OrderItem();
        orderItem.setIdOrderItem(itemId);
        List<OrderItem> items = new ArrayList<>();
        items.add(orderItem);
        order.setItems(items);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderItemRepository.findById(itemId)).thenReturn(Optional.of(orderItem));

        String orderJson = orderService.deleteItemFromOrder(orderId, itemId);

        assertNotNull(orderJson);
        verify(orderRepository, times(2)).save(order);
        verify(orderItemRepository, times(1)).deleteById(itemId);
    }

    @Test
    public void testDeleteItemFromOrderThrowsException() {
        int orderId = 1;
        int itemId = 1;
        Order order = new Order();
        order.setStatus("Waiting Checkout"); // Initialize status

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderItemRepository.findById(itemId)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> orderService.deleteItemFromOrder(orderId, itemId));

        assertEquals("Order Item with ID " + itemId + " not found", exception.getMessage());
    }
}
