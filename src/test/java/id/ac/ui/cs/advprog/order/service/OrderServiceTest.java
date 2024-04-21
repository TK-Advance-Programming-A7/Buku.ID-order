package id.ac.ui.cs.advprog.order.service;

import id.ac.ui.cs.advprog.order.model.Order;
import id.ac.ui.cs.advprog.order.model.Book;
import id.ac.ui.cs.advprog.order.model.OrderItem;
import id.ac.ui.cs.advprog.order.service.OrderService;
import id.ac.ui.cs.advprog.order.status.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderServiceTest {

    private OrderService orderService;
    private List<Order> orders;
    private List<OrderItem> orderItems;
    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        orderService = new OrderService();
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

        List<Book> books = new ArrayList<>();
        Book book1 = new Book();
        book1.setBookId(1);
        book1.setTitle("Sampo Cap Bambang");
        book1.setQuantity(2);
        books.add(book1);
        orders = new ArrayList<>();
        Order order1 = new Order(13652556,
                orderItems,
                "Depok");
        orders.add(order1);
        Order order2 = new Order(7f915bb,
                orderItems,
                "Jakarta");
        orders.add(order2);
        Order order3 = new Order(e334ef40,
                orderItems,
                "Cibinong");
        orders.add(order3);
    }

    // Additional test methods here
}
