package id.ac.ui.cs.advprog.order.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
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

        when(orderService.getOrder(1)).thenReturn(orderJson);
        when(orderService.addOrder(Mockito.any(Order.class))).thenReturn(orderJson);
        when(orderService.editOrder(Mockito.anyInt(), Mockito.any(Order.class))).thenReturn(orderJson);
        when(orderService.deleteOrder(1)).thenReturn(orderJson);
        when(orderService.getAllOrdersOfUser(1)).thenReturn(orderJson);
        when(orderService.addBookToOrder(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyFloat())).thenReturn(orderJson);
        when(orderService.decreaseBookInOrder(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(orderJson);
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

}

