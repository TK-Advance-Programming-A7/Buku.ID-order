package id.ac.ui.cs.advprog.order.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.order.model.Order;
import id.ac.ui.cs.advprog.order.model.OrderItem;
import id.ac.ui.cs.advprog.order.service.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderServiceImpl orderService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Order order;
    private String orderJson;
    private List<Order> orders;
    private ArrayList<OrderItem> orderItems;

    @BeforeEach
    void setup() throws JsonProcessingException {
        MockitoAnnotations.initMocks(this);

        // Setup order
        orders = new ArrayList<>();

        orderItems = new ArrayList<>();
        OrderItem item1 = new OrderItem();
        item1.setIdBook(1);
        item1.setAmount(2);
        item1.setPrice(10.99f);

        OrderItem item2 = new OrderItem();
        item2.setIdBook(2);
        item2.setAmount(1);
        item2.setPrice(8.50f);

        orderItems.add(item1);
        orderItems.add(item2);

        order = new Order("8886406", orderItems, "Rukita Pepaya Margonda");
        order.setTotalPrice();
        orders.add(order);

        // Convert order to JSON string
        orderJson = objectMapper.writeValueAsString(orders);

        // Stubbing orderService methods
        when(orderService.getOrder(1)).thenReturn(orderJson);
        when(orderService.getOrder(999)).thenThrow(new NoSuchElementException("No such order"));
        when(orderService.addOrder(any(Order.class))).thenReturn(orderJson);
        when(orderService.editOrder(anyInt(), any(Order.class))).thenReturn(orderJson);
        when(orderService.deleteOrder(1)).thenReturn(orderJson);
        when(orderService.getAllOrdersOfUser("1")).thenReturn(orderJson);
        when(orderService.addBookToOrder(anyInt(), anyInt(), anyInt(), anyFloat())).thenReturn(orderJson);
        when(orderService.decreaseBookInOrder(anyInt(), anyInt(), anyInt())).thenReturn(orderJson);
    }

    @Test
    void nextStatus_JsonProcessingExceptionThrown_RuntimeExceptionThrown() throws JsonProcessingException {
        // Mocking orderService to throw JsonProcessingException
        when(orderService.updateNextStatus(anyInt())).thenThrow(new JsonProcessingException("Error processing JSON") {});

        Map<String, Integer> jsonIdOrder = new HashMap<>();
        jsonIdOrder.put("idOrder", 1);

        // Verifying that a RuntimeException is thrown
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderController.nextStatus(jsonIdOrder).join();
        });

        // Verifying that the exception message matches
        assertEquals("java.util.concurrent.CompletionException: java.lang.RuntimeException: id.ac.ui.cs.advprog.order.controller.OrderControllerTest$1: Error processing JSON",
                exception.toString());
    }

    @Test
    void cancelOrder_JsonProcessingExceptionThrown_RuntimeExceptionThrown() throws JsonProcessingException {
        // Mocking orderService to throw JsonProcessingException
        when(orderService.cancelOrder(anyInt())).thenThrow(new JsonProcessingException("Error processing JSON") {});

        Map<String, Integer> jsonIdOrder = new HashMap<>();
        jsonIdOrder.put("idOrder", 1);

        // Verifying that a RuntimeException is thrown
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderController.cancelOrder(jsonIdOrder).join();
        });

        // Verifying that the exception message matches
        assertEquals("java.util.concurrent.CompletionException: java.lang.RuntimeException: id.ac.ui.cs.advprog.order.controller.OrderControllerTest$2: Error processing JSON",
                exception.toString());
    }

    @Test
    void getOrderByUserIdAndStatus_ExceptionThrown_InternalServerErrorReturned() throws JsonProcessingException {
        // Mocking orderService to throw a generic exception
        when(orderService.getOrdersByUserIdAndStatus(any(String.class), any(String.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Calling the getOrderByUserIdAndStatus method and verifying the response
        ResponseEntity<String> response = orderController.getOrderByUserIdAndStatus("1", "status");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to fetch orders.", response.getBody());
    }

    @Test
    void deleteItemFromOrder_ExceptionThrown_InternalServerErrorReturned() throws JsonProcessingException {
        // Mocking orderService to throw a generic exception
        when(orderService.deleteItemFromOrder(anyInt(), anyInt())).thenThrow(new RuntimeException("Unexpected error"));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("idOrder", 1);
        requestBody.put("idOrderItem", 1);

        // Calling the deleteItemFromOrder method and verifying the response
        ResponseEntity<String> response = orderController.deleteItemFromOrder(requestBody).join();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to delete item from order.", response.getBody());
    }

    @Test
    void addBookToOrder_InvalidRequestBody_IllegalArgumentException() throws JsonProcessingException {
        Map<String, Object> requestBody = Map.of(
                "idOrder", 1,
                "idBook", "invalidBookId",
                "quantity", 2,
                "price", "invalidPrice"
        );

        // Stub orderService to throw IllegalArgumentException
        doThrow(new IllegalArgumentException("Invalid request body")).when(orderService)
                .addBookToOrder(anyInt(), anyInt(), anyInt(), anyFloat());

        // Call the addBookToOrder method and verify the response
        ResponseEntity<String> response = orderController.addBookToOrder(requestBody).join();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to add book to order.", response.getBody());
    }

    @Test
    void decreaseBookInOrder_InvalidRequestBody_IllegalArgumentException() throws JsonProcessingException {
        Map<String, Object> requestBody = Map.of(
                "idOrder", 1,
                "idBook", "invalidBookId",
                "quantity", "invalidQuantity"
        );

        // Stub orderService to throw IllegalArgumentException
        doThrow(new IllegalArgumentException("Invalid request body")).when(orderService)
                .decreaseBookInOrder(anyInt(), anyInt(), anyInt());

        // Call the decreaseBookInOrder method and verify the response
        ResponseEntity<String> response = orderController.decreaseBookInOrder(requestBody).join();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to decrease book in order.", response.getBody());
    }

    @Test
    void getAllOrdersOfUser_ExceptionThrown_InternalServerErrorReturned() throws JsonProcessingException {
        String idUser = "1";

        // Stub orderService to throw a generic exception
        doThrow(new RuntimeException("Unexpected error")).when(orderService).getAllOrdersOfUser(idUser);

        // Call the getAllOrdersOfUser method and verify the response
        ResponseEntity<String> response = orderController.getAllOrdersOfUser(idUser);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to fetch orders.", response.getBody());
    }

    @Test
    void deleteOrder_ExceptionThrown_InternalServerErrorReturned() {
        Map<String, Integer> jsonIdOrder = Map.of("idOrder", 1);

        // Stub orderService to throw a generic exception
        doThrow(new RuntimeException("Unexpected error")).when(orderService).deleteOrder(anyInt());

        // Call the deleteOrder method and verify the response
        ResponseEntity<String> response = orderController.deleteOrder(jsonIdOrder).join();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to delete order.", response.getBody());
    }

    @Test
    void deleteOrder_OrderExists_ReturnsDeletedOrderJson() {
        Map<String, Integer> jsonIdOrder = Map.of("idOrder", 1);

        ResponseEntity<String> response = orderController.deleteOrder(jsonIdOrder).join();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderJson, response.getBody());
    }

    @Test
    void deleteOrder_OrderNotExists_ReturnsNotFound() {
        Map<String, Integer> jsonIdOrder = Map.of("idOrder", 999);

        // Stub orderService to throw NoSuchElementException
        doThrow(new NoSuchElementException("Order with the given ID not found")).when(orderService).deleteOrder(999);

        ResponseEntity<String> response = orderController.deleteOrder(jsonIdOrder).join();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Order with the given ID not found.", response.getBody());
    }

    @Test
    void getOrder_JsonProcessingExceptionThrown_RuntimeExceptionThrown() throws JsonProcessingException {
        int orderId = 1;

        // Stub orderService to throw JsonProcessingException
        when(orderService.getOrder(orderId)).thenThrow(new JsonProcessingException("Error processing JSON") {});

        // Verify that a RuntimeException is thrown
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderController.getOrder(orderId);
        });

        // Verify that the exception message contains the expected substring
        assertTrue(exception.toString().contains("Error processing JSON"));
    }


    @Test
    void getOrder_OrderExists_ReturnsOrderJson() {
        int orderId = 1;

        ResponseEntity<String> response = orderController.getOrder(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderJson, response.getBody());
    }

    @Test
    void getOrder_OrderNotExists_ReturnsNotFound() {
        int orderId = 999;

        ResponseEntity<String> response = orderController.getOrder(orderId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Order not found.", response.getBody());
    }

    @Test
    void testGetOrder() throws Exception {
        int orderId = 1;

        when(orderService.getOrder(orderId)).thenReturn(orderJson);

        ResponseEntity<String> responseEntity = orderController.getOrder(orderId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(orderJson, responseEntity.getBody());
    }


    @Test
    void getOrderNotExist() {
        int nonExistentOrderId = 999;

        ResponseEntity<String> responseEntity = orderController.getOrder(nonExistentOrderId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Order not found.", responseEntity.getBody());
    }


    @Test
    void testDeleteOrder() throws Exception {
        HashMap<String, Integer> jsonIdOrder = new HashMap<>();
        jsonIdOrder.put("idOrder", 1);

        String expectedResponse = "Deleted successfully";
        when(orderService.deleteOrder(1)).thenReturn(expectedResponse);

        CompletableFuture<ResponseEntity<String>> responseEntityFuture = orderController.deleteOrder(jsonIdOrder);

        ResponseEntity<String> responseEntity = responseEntityFuture.get();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());
    }

    @Test
    void testDeleteOrderNotExist() throws ExecutionException, InterruptedException {
        HashMap<String, Integer> jsonIdOrder = new HashMap<>();
        jsonIdOrder.put("idOrder", 999);

        when(orderService.deleteOrder(999)).thenThrow(new NoSuchElementException("Order with the given ID not found"));

        CompletableFuture<ResponseEntity<String>> responseEntityFuture = orderController.deleteOrder(jsonIdOrder);

        ResponseEntity<String> responseEntity = responseEntityFuture.get();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Order with the given ID not found.", responseEntity.getBody());
    }

    @Test
    void testGetAllOrdersOfUser(){
        String userId = "1";

        ResponseEntity<String> responseEntity = orderController.getAllOrdersOfUser(userId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(orderJson, responseEntity.getBody());
    }

    @Test
    void testGetAllOrdersOfUserNotExist() throws Exception {
        String nonExistentUserId = "999";

        when(orderService.getAllOrdersOfUser(nonExistentUserId)).thenReturn("[]");

        ResponseEntity<String> responseEntity = orderController.getAllOrdersOfUser(nonExistentUserId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("[]", responseEntity.getBody());
    }

    @Test
    void testNextStatusOrderExists() throws Exception {
        int idOrder = 1;

        Order expectedOrder = new Order();
        expectedOrder.setIdOrder(idOrder);
        String updatedOrderJson = objectMapper.writeValueAsString(expectedOrder);

        when(orderService.updateNextStatus(idOrder)).thenReturn(updatedOrderJson);

        HashMap<String, Integer> jsonIdOrder = new HashMap<>();
        jsonIdOrder.put("idOrder", idOrder);

        CompletableFuture<ResponseEntity<String>> responseEntityFuture = orderController.nextStatus(jsonIdOrder);

        ResponseEntity<String> responseEntity = responseEntityFuture.get();


        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(updatedOrderJson, responseEntity.getBody());
    }

    @Test
    void testNextStatusOrderDoesNotExist() throws Exception {
        int nonExistentIdOrder = 1000;

        when(orderService.updateNextStatus(nonExistentIdOrder)).thenThrow(new NoSuchElementException());

        HashMap<String, Integer> jsonIdOrder = new HashMap<>();
        jsonIdOrder.put("idOrder", nonExistentIdOrder);


        CompletableFuture<ResponseEntity<String>> responseEntityFuture = orderController.nextStatus(jsonIdOrder);

        ResponseEntity<String> responseEntity = responseEntityFuture.get();


        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("There is no such order.", responseEntity.getBody());
    }


    @Test
    void testDeleteItemFromOrder() throws Exception {
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("idOrder", 1);
        requestBody.put("idOrderItem", 1);

        String expectedResponse = "Item deleted successfully";
        when(orderService.deleteItemFromOrder(1, 1)).thenReturn(expectedResponse);

        CompletableFuture<ResponseEntity<String>> responseEntityFuture = orderController.deleteItemFromOrder(requestBody);

        ResponseEntity<String> responseEntity = responseEntityFuture.get();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());
    }

    @Test
    void testGetOrderByUserIdAndStatus() throws Exception {
        String userId = "1";
        String status = "Waiting Checkout";

        when(orderService.getOrdersByUserIdAndStatus(userId, status)).thenReturn(orderJson);

        ResponseEntity<String> responseEntity = orderController.getOrderByUserIdAndStatus(userId, status);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(orderJson, responseEntity.getBody());
    }

    @Test
    void testGetOrderByUserIdAndStatusFailed() throws Exception {
        String userId = "9999";
        String status = "Failed";

        when(orderService.getOrdersByUserIdAndStatus(userId, status)).thenThrow(new NoSuchElementException());

        ResponseEntity<String> responseEntity = orderController.getOrderByUserIdAndStatus(userId, status);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("User ID or status not provided.", responseEntity.getBody());
    }

    @Test
    void testDeleteItemFromOrderNotFound() throws Exception {
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("idOrder", 1);
        requestBody.put("idOrderItem", 999); // Assuming this item doesn't exist

        when(orderService.deleteItemFromOrder(1, 999)).thenThrow(new NoSuchElementException());

        CompletableFuture<ResponseEntity<String>> responseEntityFuture = orderController.deleteItemFromOrder(requestBody);

        ResponseEntity<String> responseEntity = responseEntityFuture.get();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Order or Item with the given ID not found.", responseEntity.getBody());
    }

    @Test
    void testCancelOrderSuccess() throws Exception {
        HashMap<String, Integer> jsonIdOrder = new HashMap<>();
        jsonIdOrder.put("idOrder", 1);

        String expectedResponse = "Order cancelled successfully";
        when(orderService.cancelOrder(1)).thenReturn(expectedResponse);

        CompletableFuture<ResponseEntity<String>> responseEntityFuture = orderController.cancelOrder(jsonIdOrder);

        ResponseEntity<String> responseEntity = responseEntityFuture.get();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());
    }

    @Test
    void testCancelOrderNotFound() throws Exception {
        HashMap<String, Integer> jsonIdOrder = new HashMap<>();
        jsonIdOrder.put("idOrder", 999);

        when(orderService.cancelOrder(999)).thenThrow(new NoSuchElementException("There is no such order."));

        CompletableFuture<ResponseEntity<String>> responseEntityFuture = orderController.cancelOrder(jsonIdOrder);

        ResponseEntity<String> responseEntity = responseEntityFuture.get();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("There is no such order.", responseEntity.getBody());
    }

    @Test
    void testCancelOrderNotCancelable() throws Exception {
        HashMap<String, Integer> jsonIdOrder = new HashMap<>();
        jsonIdOrder.put("idOrder", 1);

        when(orderService.cancelOrder(1)).thenThrow(new IllegalArgumentException("Order is not cancelable."));

        CompletableFuture<ResponseEntity<String>> responseEntityFuture = orderController.cancelOrder(jsonIdOrder);

        ResponseEntity<String> responseEntity = responseEntityFuture.get();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Order is not cancelable.", responseEntity.getBody());
    }

    @Test
    void testAddOrder() throws Exception {
        Order orderToAdd = new Order("12345", Collections.emptyList(), "Test Address");

        when(orderService.addOrder(any(Order.class))).thenReturn(orderJson);

        Map<String, Order> requestBody = new HashMap<>();
        requestBody.put("order", orderToAdd);

        CompletableFuture<ResponseEntity<String>> responseFuture = orderController.addOrder(requestBody);
        ResponseEntity<String> responseEntity = responseFuture.get();

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(orderJson, responseEntity.getBody());
    }

    @Test
    void testAddOrderFailure() throws Exception {
        Order orderToAdd = new Order("12345", Collections.emptyList(), "Test Address");

        when(orderService.addOrder(any(Order.class))).thenThrow(new RuntimeException("Failed to add order."));

        Map<String, Order> requestBody = new HashMap<>();
        requestBody.put("order", orderToAdd);

        CompletableFuture<ResponseEntity<String>> responseFuture = orderController.addOrder(requestBody);
        ResponseEntity<String> responseEntity = responseFuture.get();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Failed to add order.", responseEntity.getBody());
    }

    @Test
    void testEditOrder() throws Exception {
        Order orderToEdit = new Order("12345", Collections.emptyList(), "Updated Address");
        orderToEdit.setIdOrder(1);

        when(orderService.editOrder(anyInt(), any(Order.class))).thenReturn(orderJson);

        Map<String, Order> requestBody = new HashMap<>();
        requestBody.put("order", orderToEdit);

        CompletableFuture<ResponseEntity<String>> responseFuture = orderController.editOrder(requestBody);
        ResponseEntity<String> responseEntity = responseFuture.get();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(orderJson, responseEntity.getBody());
    }

    @Test
    void testEditOrderNotFound() throws Exception {
        Order orderToEdit = new Order("12345", Collections.emptyList(), "Updated Address");
        orderToEdit.setIdOrder(1);

        when(orderService.editOrder(anyInt(), any(Order.class))).thenThrow(new NoSuchElementException("Order with the given ID not found."));

        Map<String, Order> requestBody = new HashMap<>();
        requestBody.put("order", orderToEdit);

        CompletableFuture<ResponseEntity<String>> responseFuture = orderController.editOrder(requestBody);
        ResponseEntity<String> responseEntity = responseFuture.get();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Order with the given ID not found.", responseEntity.getBody());
    }

    @Test
    void testEditOrderFailure() throws Exception {
        Order orderToEdit = new Order("12345", Collections.emptyList(), "Updated Address");
        orderToEdit.setIdOrder(1);

        when(orderService.editOrder(anyInt(), any(Order.class))).thenThrow(new RuntimeException("Failed to edit order."));

        Map<String, Order> requestBody = new HashMap<>();
        requestBody.put("order", orderToEdit);

        CompletableFuture<ResponseEntity<String>> responseFuture = orderController.editOrder(requestBody);
        ResponseEntity<String> responseEntity = responseFuture.get();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Failed to edit order.", responseEntity.getBody());
    }


    @Test
    void testAddBookToOrderSuccess() throws Exception {
        int orderId = 1;
        int bookId = 1;
        int quantity = 2;
        float price = 10.0f;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("idOrder", orderId);
        requestBody.put("idBook", bookId);
        requestBody.put("quantity", quantity);
        requestBody.put("price", price);

        String addedBookToOrderJson = "Added book to order JSON";

        when(orderService.addBookToOrder(orderId, bookId, quantity, price)).thenReturn(addedBookToOrderJson);

        CompletableFuture<ResponseEntity<String>> responseFuture = orderController.addBookToOrder(requestBody);
        ResponseEntity<String> responseEntity = responseFuture.get();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(addedBookToOrderJson, responseEntity.getBody());
    }

    @Test
    void testAddBookToOrderNotFound() throws Exception {
        int orderId = 1;
        int bookId = 1;
        int quantity = 2;
        float price = 10.0f;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("idOrder", orderId);
        requestBody.put("idBook", bookId);
        requestBody.put("quantity", quantity);
        requestBody.put("price", price);

        when(orderService.addBookToOrder(orderId, bookId, quantity, price)).thenThrow(new NoSuchElementException());

        CompletableFuture<ResponseEntity<String>> responseFuture = orderController.addBookToOrder(requestBody);
        ResponseEntity<String> responseEntity = responseFuture.get();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Order or Book with the given ID not found.", responseEntity.getBody());
    }

    @Test
    void testAddBookToOrderFailed() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();

        CompletableFuture<ResponseEntity<String>> responseFuture = orderController.addBookToOrder(requestBody);
        ResponseEntity<String> responseEntity = responseFuture.get();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Failed to add book to order.", responseEntity.getBody());
    }

    @Test
    void testDecreaseBookInOrderSuccess() throws Exception {
        int orderId = 1;
        int bookId = 1;
        int quantity = 1;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("idOrder", orderId);
        requestBody.put("idBook", bookId);
        requestBody.put("quantity", quantity);

        String decreasedBookInOrderJson = "Decreased book in order JSON";

        when(orderService.decreaseBookInOrder(orderId, bookId, quantity)).thenReturn(decreasedBookInOrderJson);

        CompletableFuture<ResponseEntity<String>> responseFuture = orderController.decreaseBookInOrder(requestBody);
        ResponseEntity<String> responseEntity = responseFuture.get();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(decreasedBookInOrderJson, responseEntity.getBody());
    }

    @Test
    void testDecreaseBookInOrderNotFound() throws Exception {
        int orderId = 1;
        int bookId = 1;
        int quantity = 1;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("idOrder", orderId);
        requestBody.put("idBook", bookId);
        requestBody.put("quantity", quantity);

        when(orderService.decreaseBookInOrder(orderId, bookId, quantity)).thenThrow(new NoSuchElementException());

        CompletableFuture<ResponseEntity<String>> responseFuture = orderController.decreaseBookInOrder(requestBody);
        ResponseEntity<String> responseEntity = responseFuture.get();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Order or Book with the given ID not found.", responseEntity.getBody());
    }

    @Test
    void testDecreaseBookFailed() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();

        CompletableFuture<ResponseEntity<String>> responseFuture = orderController.decreaseBookInOrder(requestBody);
        ResponseEntity<String> responseEntity = responseFuture.get();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Failed to decrease book in order.", responseEntity.getBody());
    }
}