package id.ac.ui.cs.advprog.order.repository;

import id.ac.ui.cs.advprog.order.model.OrderItem;
import id.ac.ui.cs.advprog.order.repository.OrderRepository;
import id.ac.ui.cs.advprog.order.model.Order;
import id.ac.ui.cs.advprog.order.model.Book;

import id.ac.ui.cs.advprog.order.status.WaitingDeliveredState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class OrderRepositoryTest {
    private OrderRepository orderRepository;
    private List <Order> orders;
    private Map<Integer, OrderItem> orderItems;
    private Order order;

    @BeforeEach
    void setUp() {

        orderRepository = Mockito.mock(OrderRepository.class);

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
    }

    @Test
    void testSaveCreate() {
        Order order = orders.get(0);

        Mockito.when(orderRepository.save(order)).thenReturn(order);
        Mockito.when(orderRepository.findById(order.getIdOrder())).thenReturn(Optional.of(order));

        Order result = orderRepository.save(order);

        Optional<Order> findResult = orderRepository.findById(orders.get(0).getIdOrder());

        assertEquals(order.getIdOrder(), result.getIdOrder());
        assertEquals(order.getIdOrder(), findResult.get().getIdOrder());
        assertEquals(order.getIdUser(), findResult.get().getIdUser());
        assertEquals(order.getOrderDate(), findResult.get().getOrderDate());
        assertEquals(order.getStatus(), findResult.get().getStatus());
        assertEquals(order.getAddress(), findResult.get().getAddress());
        assertEquals(order.isCancelable(), findResult.get().isCancelable());
        assertEquals(order.getTotalPrice(), findResult.get().getTotalPrice());
        assertSame(order.getItems(), findResult.get().getItems());
    }

    @Test
    void testSaveUpdate() {
        Order order = orders.get(0);

        order.setAddress("Jl. Pepaya no.33");
        order.setStatus(new WaitingDeliveredState());

        Mockito.when(orderRepository.save(order)).thenReturn(order);
        Mockito.when(orderRepository.findById(order.getIdOrder())).thenReturn(Optional.of(order));

        orderRepository.save(order);

        Optional<Order> findResultOptional = orderRepository.findById(order.getIdOrder());
        Order findResult = findResultOptional.get();

        assertEquals(order.getIdOrder(), findResult.getIdOrder());
        assertEquals(order.getAddress(), findResult.getAddress());
        assertEquals(order.getStatus(), findResult.getStatus());
        assertEquals(order.isCancelable(), findResult.isCancelable());
        assertEquals(order.getTotalPrice(), findResult.getTotalPrice());
    }

    @Test
    void testFindByIdIfIdFound() {

        Mockito.when(orderRepository.save(order)).thenReturn(order);
        Mockito.when(orderRepository.findById(order.getIdOrder())).thenReturn(Optional.of(order));

        orderRepository.saveAll(orders);

        Optional<Order> findResultOptional = orderRepository.findById(order.getIdOrder());
        Order findResult = findResultOptional.get();
        assertSame(orders.getFirst(), findResult);
    }

    @Test
    void testFindByIdIfIdNotFound() {
        Mockito.when(orderRepository.save(order)).thenReturn(order);
        Mockito.when(orderRepository.findById(order.getIdOrder())).thenReturn(Optional.of(order));

        orderRepository.saveAll(orders);

        Mockito.when(orderRepository.findById(-1)).thenReturn(Optional.empty());

        Optional<Order> findResultOptional = orderRepository.findById(-1);
        assertTrue(findResultOptional.isEmpty());
    }

    @Test
    void testFindAllByUserIfUserCorrect() {
        Mockito.when(orderRepository.save(order)).thenReturn(order);
        Mockito.when(orderRepository.findAllByIdUser(orders.get(0).getIdUser())).thenReturn(orders);

        orderRepository.saveAll(orders);

        List <Order> orderList = orderRepository.findAllByIdUser(
                orders.get(0).getIdUser());
        assertEquals(orders.size(), orderList.size());
    }

    @Test
    void testFindAllByUserIfUserIncorrect() {
        Mockito.when(orderRepository.saveAll(orders)).thenReturn(orders);
        Mockito.when(orderRepository.findAllByIdUser(-1)).thenReturn(new ArrayList<>());

        orderRepository.saveAll(orders);

        List<Order> orderList = orderRepository.findAllByIdUser(-1);
        assertTrue(orderList.isEmpty());
    }

}