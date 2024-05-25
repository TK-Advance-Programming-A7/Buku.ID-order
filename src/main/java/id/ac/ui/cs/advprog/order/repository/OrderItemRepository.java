package id.ac.ui.cs.advprog.order.repository;

import id.ac.ui.cs.advprog.order.model.Order;
import id.ac.ui.cs.advprog.order.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface
OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    OrderItem findByOrderAndIdBook(Order order, int idBook);

    List<OrderItem> findByOrder(Order order);
}