package id.ac.ui.cs.advprog.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.ac.ui.cs.advprog.order.model.Order;
import id.ac.ui.cs.advprog.order.model.OrderItem;

import java.util.List;

public interface OrderService {
    String updateNextStatus(int idOrder) throws JsonProcessingException;

    String findAll() throws JsonProcessingException;

    String getOrder(int idOrder) throws JsonProcessingException;

    String addOrder(Order order) throws JsonProcessingException;

    String getOrderState(int idOrder);

    List<OrderItem> getOrderItemsByOrder(int idOrder);

    String editOrder(int idOrder, Order updatedOrder) throws JsonProcessingException;

    String getAllOrdersOfUser(String idUser) throws JsonProcessingException;

    String deleteOrder(int idOrder);

    String addBookToOrder(int idOrder, int idBook, int quantity, float price) throws JsonProcessingException;

    String decreaseBookInOrder(int idOrder, int idBook, int quantity) throws JsonProcessingException;

    String cancelOrder(int idOrder) throws JsonProcessingException;

    String deleteItemFromOrder(int idOrder, int idItem) throws JsonProcessingException;

    String getOrdersByUserIdAndStatus(String idUser, String status) throws JsonProcessingException;


}
