package id.ac.ui.cs.advprog.order.service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.*;
import id.ac.ui.cs.advprog.order.model.Order;
import id.ac.ui.cs.advprog.order.model.OrderItem;
import id.ac.ui.cs.advprog.order.repository.OrderRepository;
import id.ac.ui.cs.advprog.order.repository.OrderItemRepository;
import id.ac.ui.cs.advprog.order.status.CancelledState;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;


@Service
public class OrderServiceImpl implements OrderService{

    private final OrderRepository repository;
    private final OrderItemRepository orderItemRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String ORDER_NOT_FOUND = "Order with ID ";
    private static final String NOT_FOUND = " not found";

    @Autowired
    public OrderServiceImpl(OrderRepository repository, OrderItemRepository orderItemRepository) {
        this.repository = repository;
        this.orderItemRepository = orderItemRepository;
    }

    private Order findOrderById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(ORDER_NOT_FOUND + id + NOT_FOUND));
    }

    public String updateNextStatus(int idOrder) throws JsonProcessingException {
        Order order = findOrderById(idOrder);

        order.nextStatus();
        repository.save(order);
        return objectMapper.writeValueAsString(order);
    }


    public String findAll() throws JsonProcessingException {
        List<Order> orders = repository.findAll();
        orders.forEach(order -> order.setStatus(order.getStatus()));
        return objectMapper.writeValueAsString(orders);
    }

    public String getOrder(int idOrder) throws JsonProcessingException {
        Order order = findOrderById(idOrder);
        order.setStatus(order.getStatus());
        return objectMapper.writeValueAsString(order);
    }


    public String addOrder(Order order) throws JsonProcessingException {
        repository.save(order);
        order.setStatus(order.getStatus());
        return objectMapper.writeValueAsString(order);
    }


    public String getOrderState(int idOrder) {
        Order order = findOrderById(idOrder);
        return new Gson().toJson(order.getState().toString());
    }

    public List<OrderItem> getOrderItemsByOrder(int idOrder) {
        return orderItemRepository.findByOrder(repository.findById(idOrder).orElseThrow(() -> new NoSuchElementException(ORDER_NOT_FOUND + idOrder + NOT_FOUND)));
    }


    public String editOrder(int idOrder, Order updatedOrder) throws JsonProcessingException {
        Order order = findOrderById(idOrder);

        order.setItems(updatedOrder.getItems());
        order.setIdUser(updatedOrder.getIdUser());
        order.setStatus(updatedOrder.getStatus());
        order.setAddress(updatedOrder.getAddress());
        order.setOrderDate(updatedOrder.getOrderDate());

        order.setTotalPrice();
        repository.save(order);
        return objectMapper.writeValueAsString(order);
    }


    public String getAllOrdersOfUser(String userId) throws JsonProcessingException {
        List<Order> orders = repository.findAllByIdUser(userId);
        orders.forEach(order -> order.setStatus(order.getStatus()));  // This reinitializes the transient State based on the persistent Status

        return objectMapper.writeValueAsString(orders);
    }

    public String deleteOrder(int idOrder) {
        Order order = findOrderById(idOrder);
        repository.delete(order);
        return new Gson().toJson("Delete is successful.");
    }

    OrderItem findOrderItemByBookId(Order order, int bookId) {
        return order.getItems().stream()
                .filter(item -> item.getIdBook() == bookId)
                .findFirst()
                .orElse(null);
    }

    public String addBookToOrder(int orderId, int bookId, int quantity, float price) throws JsonProcessingException {
        Order order = findOrderById(orderId);
        OrderItem item = findOrderItemByBookId(order, bookId);

        if (item != null) {
            item.setAmount(item.getAmount() + quantity);
        } else {
            item = new OrderItem();
            item.setOrder(order);
            item.setIdBook(bookId);
            item.setAmount(quantity);
            item.setPrice(price);
            order.getItems().add(item);
        }
        orderItemRepository.save(item);
        order.setTotalPrice();
        order.setStatus(order.getStatus());
        repository.save(order);
        return objectMapper.writeValueAsString(order);
    }

    public String decreaseBookInOrder(int orderId, int bookId, int quantity) throws JsonProcessingException {
        Order order = findOrderById(orderId);
        OrderItem item = findOrderItemByBookId(order, bookId);

        if (item != null) {
            int newQuantity = item.getAmount() - quantity;
            if (newQuantity <= 0) {
                order.getItems().removeIf(obj -> obj.getIdOrderItem() == item.getIdOrderItem());
                repository.save(order);
                orderItemRepository.deleteById(item.getIdOrderItem());
            } else {
                item.setAmount(newQuantity);
            }
            order.setTotalPrice();
        } else {
            throw new NoSuchElementException(String.format("Book with ID %d not found in the order.", bookId));
        }

        order.setStatus(order.getStatus());
        repository.save(order);
        return objectMapper.writeValueAsString(order);
    }

    public String cancelOrder(int idOrder) throws JsonProcessingException {
        Order order = findOrderById(idOrder);
        if(order.isCancelable()){
            order.setState(new CancelledState());
        }
        else{
            throw new IllegalArgumentException(ORDER_NOT_FOUND + idOrder + NOT_FOUND);
        }

        repository.save(order);
        return getOrder(idOrder);
    }

    @Transactional
    public String deleteItemFromOrder(int orderId, int itemId) throws JsonProcessingException {
        Order order = findOrderById(orderId);
        OrderItem item = findOrderItemById(itemId);

        if (item != null) {
            order.getItems().removeIf(obj -> obj.getIdOrderItem() == itemId);
            repository.save(order); // Save the order back to the database
            orderItemRepository.deleteById(itemId); // Delete the item from the database
            order.setTotalPrice();
        } else {
            throw new NoSuchElementException(String.format("Item with ID %d not found in the order.", itemId));
        }

        repository.save(order);
        return objectMapper.writeValueAsString(order);
    }


    OrderItem findOrderItemById(int id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order Item with ID " + id + NOT_FOUND));
    }

    public String getOrdersByUserIdAndStatus(String userId, String status) throws JsonProcessingException {
        List<Order> orders = repository.findAllByIdUserAndStatus(userId, status);
        orders.forEach(order -> order.setStatus(order.getStatus()));
        return objectMapper.writeValueAsString(orders);
    }

}