package id.ac.ui.cs.advprog.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.ac.ui.cs.advprog.order.model.Order;
import id.ac.ui.cs.advprog.order.model.OrderItem;

import java.util.List;

public interface OrderService {
    String updateNextStatus(int id_order) throws JsonProcessingException;
    String findAll() throws JsonProcessingException;
    String getOrder(int id_order) throws JsonProcessingException;
    String addOrder(Order order) throws JsonProcessingException;
    String getOrderState(int id_order);
    List<OrderItem> getOrderItemsByOrder(int order);
    String editOrder(int idOrder, Order updatedOrder) throws JsonProcessingException;
    String getAllOrdersOfUser(int userId) throws JsonProcessingException;
    String getAllOrdersOfUserByStatus(int userId, String status) throws JsonProcessingException;
    String deleteOrder(int idOrder);
    String addBookToOrder(int orderId, int bookId, int quantity, float price) throws JsonProcessingException;
    String decreaseBookInOrder(int orderId, int bookId, int quantity) throws JsonProcessingException;
    String cancelOrder(int id_order) throws JsonProcessingException;
}
