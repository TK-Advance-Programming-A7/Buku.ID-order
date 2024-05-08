package id.ac.ui.cs.advprog.order.service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.*;
import id.ac.ui.cs.advprog.order.model.Order;
import id.ac.ui.cs.advprog.order.model.OrderItem;
import id.ac.ui.cs.advprog.order.repository.OrderRepository;
import id.ac.ui.cs.advprog.order.repository.OrderItemRepository;
import id.ac.ui.cs.advprog.order.status.CancelledState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;


@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository repository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Runnable createRunnable(Order order){
        Runnable aRunnable = new Runnable(){
            public void run(){
                try {
                    updateNextStatus(order.getIdOrder());
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        };
        return aRunnable;
    }

    private Order findOrderById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order with ID " + id + " not found"));
    }

    public String updateNextStatus(int id_order) throws JsonProcessingException {
        Order order = findOrderById(id_order);

        order.nextStatus();
        repository.save(order);
        return objectMapper.writeValueAsString(order);
    }


    public String findAll() throws JsonProcessingException {
        List<Order> orders = (List<Order>) repository.findAll();
        orders.forEach(order -> order.setStatus(order.getStatus()));
        return objectMapper.writeValueAsString(orders);
    }

    public String getOrder(int id_order) throws JsonProcessingException {
        Order order = findOrderById(id_order);
        order.setStatus(order.getStatus());
        return objectMapper.writeValueAsString(order);
    }


    public String addOrder(Order order) throws JsonProcessingException {
        repository.save(order);
        order.setStatus(order.getStatus());
        return objectMapper.writeValueAsString(order);
    }


    public String getOrderState(int id_order) {
        Order order = findOrderById(id_order);
        String state = new Gson().toJson(order.getState().toString());
        return state;
    }

    public List<OrderItem> getOrderItemsByOrder(int order) {
        return orderItemRepository.findByOrder(repository.findById(order).orElseThrow(() -> new NoSuchElementException("Order with ID " + order + " not found")));
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


    public String getAllOrdersOfUser(int userId) throws JsonProcessingException {
        List<Order> orders = repository.findAllByIdUser(userId);
        orders.forEach(order -> order.setStatus(order.getStatus()));  // This reinitializes the transient State based on the persistent Status

        return objectMapper.writeValueAsString(orders);
    }

    public String deleteOrder(int idOrder) {
        Order order = findOrderById(idOrder);
        repository.delete(order);
        return new Gson().toJson("Delete is successful.");
    }

    private OrderItem findOrderItemByBookId(Order order, int bookId) {
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
                order.getItems().remove(item);
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

    public String cancelOrder(int id_order) throws JsonProcessingException {
        Order order = findOrderById(id_order);
        if(order.isCancelable()){
            order.setState(new CancelledState());
        }
        else{
            throw new IllegalArgumentException("Order with ID " + id_order + " cannot be canceled.");
        }

        repository.save(order);
        return getOrder(id_order);
    }



}