package id.ac.ui.cs.advprog.order.controller;

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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;



@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    private Order order;
    private Order order2;
    private String orderJson;
    private List<Order> orders;
    private Map<Integer, OrderItem> orderItems;

    @BeforeEach
    public void setup() {
        // Setup order
        Book book1 = new Book(1, "Sampo Cap Bambang", "Bambang", "Bambang CV", 10.99f, 100, "1234567890", "sampo_cap_bambang.jpg", new Date(), "Children's Books", 50, "A children's book about Sampo Cap Bambang adventures.");
        Book book2 = new Book(2, "The Adventures of Sherlock Holmes", "Arthur Conan Doyle", "Penguin Classics", 8.50f, 75, "9780140439070", "sherlock_holmes.jpg", new Date(), "Mystery", 320, "A collection of twelve stories featuring Sherlock Holmes, a consulting detective.");

        orders = new ArrayList<>();

        orderItems = new HashMap<>();
        OrderItem item1 = new OrderItem();
        item1.setIdBook(book1.getIdBook());
        item1.setAmount(2);
        item1.setPrice(book1.getPrice());

        OrderItem item2 = new OrderItem();
        item2.setIdBook(book1.getIdBook());
        item2.setAmount(1);
        item2.setPrice(book2.getPrice());

        orderItems.put(item1.getIdBook(), item1);
        orderItems.put(item2.getIdBook(), item2);

        order = new Order(8886406, orderItems, "Rukita Pepaya Margonda");
        order.setTotalPrice();
        orders.add(order);
        order2 = new Order(8886, orderItems, "Jl. Kesenangan");

        // Convert order to JSON string
        orderJson = new Gson().toJson(order);

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