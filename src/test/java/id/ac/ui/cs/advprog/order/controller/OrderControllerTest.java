package id.ac.ui.cs.advprog.order.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import id.ac.ui.cs.advprog.order.model.Book;
import id.ac.ui.cs.advprog.order.model.Order;
import id.ac.ui.cs.advprog.order.model.OrderItem;
import id.ac.ui.cs.advprog.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Order order;
    private String orderJson;
    private List<Order> orders;
    private ArrayList<OrderItem> orderItems;

    @BeforeEach
    public void setup() throws JsonProcessingException {
        // Setup order
        Book book1 = new Book(1, "Sampo Cap Bambang", "Bambang", "Bambang CV", 10.99f, 100, "1234567890", "sampo_cap_bambang.jpg", new Date(), "Children's Books", 50, "A children's book about Sampo Cap Bambang adventures.");
        Book book2 = new Book(2, "The Adventures of Sherlock Holmes", "Arthur Conan Doyle", "Penguin Classics", 8.50f, 75, "9780140439070", "sherlock_holmes.jpg", new Date(), "Mystery", 320, "A collection of twelve stories featuring Sherlock Holmes, a consulting detective.");

        orders = new ArrayList<>();

        orderItems = new ArrayList<>();
        OrderItem item1 = new OrderItem();
        item1.setIdBook(book1.getIdBook());
        item1.setAmount(2);
        item1.setPrice(book1.getPrice());

        OrderItem item2 = new OrderItem();
        item2.setIdBook(book1.getIdBook());
        item2.setAmount(1);
        item2.setPrice(book2.getPrice());

        orderItems.add(item1);
        orderItems.add(item2);

        order = new Order(8886406, orderItems, "Rukita Pepaya Margonda");
        order.setTotalPrice();
        orders.add(order);

        // Convert order to JSON string
        orderJson = objectMapper.writeValueAsString(orders);

        when(orderService.getOrder(1)).thenReturn(orderJson);
        when(orderService.addOrder(Mockito.any(Order.class))).thenReturn(orderJson);
        when(orderService.editOrder(anyInt(), Mockito.any(Order.class))).thenReturn(orderJson);
        when(orderService.deleteOrder(1)).thenReturn(orderJson);
        when(orderService.getAllOrdersOfUser(1)).thenReturn(orderJson);
        when(orderService.addBookToOrder(anyInt(), anyInt(), anyInt(), Mockito.anyFloat())).thenReturn(orderJson);
        when(orderService.decreaseBookInOrder(anyInt(), anyInt(), anyInt())).thenReturn(orderJson);
    }

    @Test
    public void testGetOrder() throws Exception {
        Map<String, Integer> jsonIdOrder = new HashMap<>();
        jsonIdOrder.put("idOrder", 1);

        mockMvc.perform(post("/api/v1/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(jsonIdOrder)))
                .andExpect(status().isOk())
                .andExpect(content().json(orderJson));
    }

    @Test
    public void getOrderNotExist() throws Exception {
        Mockito.when(orderService.getOrder(anyInt())).thenThrow(new NoSuchElementException("No such order"));
        HashMap<String, Integer> requestContent = new HashMap<>();
        requestContent.put("idOrder", 999); // Non-existing ID
        mockMvc.perform(post("/api/v1/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestContent)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("There is no such order."));
    }

    @Test
    public void testEditOrder() throws Exception {
        Map<String, Order> requestBody = new HashMap<>();
        requestBody.put("order", order);

        String requestBodyJson = objectMapper.writeValueAsString(requestBody);

        mockMvc.perform(patch("/api/v1/order/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isOk())
                .andExpect(content().json(orderJson));
    }

    @Test
    public void testDeleteOrder() throws Exception {
        Map<String, Integer> jsonIdOrder = new HashMap<>();
        jsonIdOrder.put("idOrder", 1);

        String expectedResponse = "Deleted successfully";
        Mockito.when(orderService.deleteOrder(1)).thenReturn(expectedResponse);

        mockMvc.perform(delete("/api/v1/order/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonIdOrder)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    public void testDeleteOrderNotExist() throws Exception {
        Map<String, Integer> jsonIdOrder = new HashMap<>();
        jsonIdOrder.put("idOrder", 999); // ID that does not exist

        Mockito.when(orderService.deleteOrder(999)).thenThrow(new NoSuchElementException("Order with the given ID not found"));

        mockMvc.perform(delete("/api/v1/order/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonIdOrder)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Order with the given ID not found.")); // Check the response message
    }


    @Test
    public void testGetAllOrdersOfUser() throws Exception {
        Map<String, Integer> jsonIdUser = new HashMap<>();
        jsonIdUser.put("idUser", 1);

        mockMvc.perform(get("/api/v1/order/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonIdUser)))
                .andExpect(status().isOk())
                .andExpect(content().json(orderJson));
    }

    @Test
    public void testGetAllOrdersOfUserNotExist() throws Exception {
        Map<String, Integer> jsonIdUser = new HashMap<>();
        jsonIdUser.put("idUser", 999);

        when(orderService.getAllOrdersOfUser(999)).thenReturn("[]");

        mockMvc.perform(get("/api/v1/order/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonIdUser)))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }


}

