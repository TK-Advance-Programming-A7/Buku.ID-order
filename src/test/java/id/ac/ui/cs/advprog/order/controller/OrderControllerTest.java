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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
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
        when(orderService.addOrder(Mockito.any(Order.class))).thenReturn(orderJson);
        when(orderService.editOrder(anyInt(), Mockito.any(Order.class))).thenReturn(orderJson);
        when(orderService.deleteOrder(1)).thenReturn(orderJson);
        when(orderService.getAllOrdersOfUser("1")).thenReturn(orderJson);
        when(orderService.addBookToOrder(anyInt(), anyInt(), anyInt(), Mockito.anyFloat())).thenReturn(orderJson);
        when(orderService.decreaseBookInOrder(anyInt(), anyInt(), anyInt())).thenReturn(orderJson);
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


}