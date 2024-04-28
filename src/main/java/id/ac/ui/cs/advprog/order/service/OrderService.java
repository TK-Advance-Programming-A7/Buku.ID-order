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
public class OrderService {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Runnable createRunnable(Order order){
        Runnable aRunnable = new Runnable(){
            public void run(){
                updateNextStatus(order);
            }
        };
        return aRunnable;
    }

    public void updateNextStatus(Order order) {
        order.nextStatus();
        repository.save(order);
    }

    public String findAll() throws JsonProcessingException {
        List<Order> orders = (List<Order>) repository.findAll();
        orders.forEach(order -> order.setStatus(order.getStatus()));  // Reinitialize state for each order
        return objectMapper.writeValueAsString(orders);
    }


    public String getOrder(int id_order) throws JsonProcessingException {
        Optional<Order> orderOptional = repository.findById(id_order);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setStatus(order.getStatus());
            return objectMapper.writeValueAsString(order);
        } else {
            throw new NoSuchElementException("Order with ID " + id_order + " not found");
        }
    }

    public String addOrder(Order order) throws JsonProcessingException {
        repository.save(order);
        order.setStatus(order.getStatus());
        return objectMapper.writeValueAsString(order);
    }

    public String getOrderState(int id_order) {
        Optional<Order> orderOptional = repository.findById(id_order);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            String state = new Gson().toJson(order.getState().toString());
            return state;
        } else {
            throw new NoSuchElementException();
        }
    }

    public String cancelOrder(int id_order) throws JsonProcessingException {
        Optional<Order> orderOptional = repository.findById(id_order);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setState(new CancelledState());
            order.setStatus(order.getStatus());
            repository.save(order);
            return this.getOrder(id_order);
        } else {
            throw new NoSuchElementException();
        }
    }

    public List<OrderItem> getOrderItemsByOrder(int order) {
        return orderItemRepository.findByOrder(repository.findById(order).orElseThrow(() -> new NoSuchElementException("Order with ID " + order + " not found")));
    }

    public String addBookToOrder(int orderId, int bookId, int quantity, float price) throws JsonProcessingException {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order with ID " + orderId + " not found"));

//        OrderItem item = orderItemRepository.findByOrderAndIdBook(order, bookId);
        OrderItem item = null;
        for (OrderItem itemIn : order.getItems()) {
            if (itemIn.getIdBook() == bookId){
                item = itemIn;
                break;
            }
        }
        // Find if the book already exists in the order items
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
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order with ID " + orderId + " not found"));
        try {
//            OrderItem item = orderItemRepository.findByOrderAndIdBook(order, bookId);
            OrderItem item = null;
            for (OrderItem itemIn : order.getItems()) {
                if (itemIn.getIdBook() == bookId){
                    item = itemIn;
                    break;
                }
            }

            if (item != null) {
                int newQuantity = item.getAmount() - quantity;
                if (newQuantity <= 0) {
//                    order.getItems().remove(bookId);
                    orderItemRepository.delete(item);
                } else {
                    item.setAmount(newQuantity);
                }
                order.setTotalPrice();
            } else {
                throw new NoSuchElementException("Book with ID " + bookId + " not found in the order.");
            }
            repository.save(order);
            order.setStatus(order.getStatus());
            return objectMapper.writeValueAsString(order);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Book with ID " + bookId + " not found in the order.");
        }
    }

    public String editOrder(int idOrder, Order updatedOrder) throws JsonProcessingException {
        Optional<Order> orderOptional = repository.findById(idOrder);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            // Update order properties with values from updatedOrder
            order.setItems(updatedOrder.getItems());
            order.setIdUser(updatedOrder.getIdUser());
            order.setStatus(updatedOrder.getStatus());
            order.setAddress(updatedOrder.getAddress());
            order.setOrderDate(updatedOrder.getOrderDate());
            order.setTotalPrice();

            repository.save(order);
            return objectMapper.writeValueAsString(order);
        } else {
            throw new NoSuchElementException("Order with ID " + idOrder + " not found");
        }
    }

    public String deleteOrder(int idOrder) {
        Optional<Order> orderOptional = repository.findById(idOrder);
        if (orderOptional.isPresent()) {
            repository.deleteById(idOrder);
            return new Gson().toJson("Delete is succesful.");
        } else {
            throw new NoSuchElementException("Order with ID " + idOrder + " not found");
        }
    }

    public String getAllOrdersOfUser(int userId) throws JsonProcessingException {
        List<Order> orders = repository.findAllByIdUser(userId);
        orders.forEach(order -> order.setStatus(order.getStatus()));  // This reinitializes the transient State based on the persistent Status

        return objectMapper.writeValueAsString(orders);
    }



}