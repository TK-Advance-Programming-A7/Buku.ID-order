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

public class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderServiceImpl orderService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Order order;
    private String orderJson;
    private List<Order> orders;
    private ArrayList<OrderItem> orderItems;

    @BeforeEach
    public void setup() throws JsonProcessingException {
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

        order = new Order(8886406, orderItems, "Rukita Pepaya Margonda");
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
        when(orderService.getAllOrdersOfUser(1)).thenReturn(orderJson);
        when(orderService.addBookToOrder(anyInt(), anyInt(), anyInt(), Mockito.anyFloat())).thenReturn(orderJson);
        when(orderService.decreaseBookInOrder(anyInt(), anyInt(), anyInt())).thenReturn(orderJson);
    }

    @Test
    public void testGetOrder() throws Exception {
        HashMap<String, Integer> jsonIdOrder = new HashMap<>();
        jsonIdOrder.put("idOrder", 1);

        CompletableFuture<ResponseEntity<?>> responseEntityFuture = orderController.getOrder(jsonIdOrder);

        ResponseEntity<?> responseEntity = responseEntityFuture.get();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(orderJson, responseEntity.getBody());
    }

    @Test
    public void getOrderNotExist() throws ExecutionException, InterruptedException {
        HashMap<String, Integer> requestContent = new HashMap<>();
        requestContent.put("idOrder", 999);

        CompletableFuture<ResponseEntity<?>> responseEntityFuture = orderController.getOrder(requestContent);

        ResponseEntity<?> responseEntity = responseEntityFuture.get();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("There is no such order.", responseEntity.getBody());
    }

    @Test
    public void testDeleteOrder() throws Exception {
        HashMap<String, Integer> jsonIdOrder = new HashMap<>();
        jsonIdOrder.put("idOrder", 1);

        String expectedResponse = "Deleted successfully";
        when(orderService.deleteOrder(1)).thenReturn(expectedResponse);

        CompletableFuture<ResponseEntity<?>> responseEntityFuture = orderController.deleteOrder(jsonIdOrder);

        ResponseEntity<?> responseEntity = responseEntityFuture.get();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());
    }

    @Test
    public void testDeleteOrderNotExist() throws ExecutionException, InterruptedException {
        HashMap<String, Integer> jsonIdOrder = new HashMap<>();
        jsonIdOrder.put("idOrder", 999);

        when(orderService.deleteOrder(999)).thenThrow(new NoSuchElementException("Order with the given ID not found"));

        CompletableFuture<ResponseEntity<?>> responseEntityFuture = orderController.deleteOrder(jsonIdOrder);

        ResponseEntity<?> responseEntity = responseEntityFuture.get();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Order with the given ID not found.", responseEntity.getBody());
    }

    @Test
    public void testGetAllOrdersOfUser() throws Exception {
        HashMap<String, Integer> jsonIdUser = new HashMap<>();
        jsonIdUser.put("idUser", 1);

        when(orderService.getAllOrdersOfUser(1)).thenReturn(orderJson);
        CompletableFuture<ResponseEntity<?>> responseEntityFuture = orderController.getAllOrdersOfUser(jsonIdUser);

        ResponseEntity<?> responseEntity = responseEntityFuture.get();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(orderJson, responseEntity.getBody());
    }

    @Test
    public void testGetAllOrdersOfUserNotExist() throws Exception {
        HashMap<String, Integer> jsonIdUser = new HashMap<>();
        jsonIdUser.put("idUser", 999);

        when(orderService.getAllOrdersOfUser(999)).thenReturn("[]");

        CompletableFuture<ResponseEntity<?>> responseEntityFuture = orderController.getAllOrdersOfUser(jsonIdUser);

        ResponseEntity<?> responseEntity = responseEntityFuture.get();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("[]", responseEntity.getBody());
    }

    @Test
    public void testNextStatusOrderExists() throws Exception {
        int idOrder = 1;

        Order expectedOrder = new Order();
        expectedOrder.setIdOrder(idOrder);
        String updatedOrderJson = objectMapper.writeValueAsString(expectedOrder);

        when(orderService.updateNextStatus(idOrder)).thenReturn(updatedOrderJson);

        HashMap<String, Integer> jsonIdOrder = new HashMap<>();
        jsonIdOrder.put("idOrder", idOrder);

        CompletableFuture<ResponseEntity<?>> responseEntityFuture = orderController.nextStatus(jsonIdOrder);

        ResponseEntity<?> responseEntity = responseEntityFuture.get();


        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(updatedOrderJson, responseEntity.getBody());
    }

    @Test
    public void testNextStatusOrderDoesNotExist() throws Exception {
        int nonExistentIdOrder = 1000;

        when(orderService.updateNextStatus(nonExistentIdOrder)).thenThrow(new NoSuchElementException());

        HashMap<String, Integer> jsonIdOrder = new HashMap<>();
        jsonIdOrder.put("idOrder", nonExistentIdOrder);


        CompletableFuture<ResponseEntity<?>> responseEntityFuture = orderController.nextStatus(jsonIdOrder);

        ResponseEntity<?> responseEntity = responseEntityFuture.get();


        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("There is no such order.", responseEntity.getBody());
    }


}
