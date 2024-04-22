package id.ac.ui.cs.advprog.order.service;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import id.ac.ui.cs.advprog.order.model.Order;
import id.ac.ui.cs.advprog.order.repository.OrderRepository;
import id.ac.ui.cs.advprog.order.status.CancelledState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;


import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository repository;

    private TaskScheduler scheduler;

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

    public String findAll() {
        List<Order> orders = (List<Order>) repository.findAll();
        String json_orders = new Gson().toJson(orders);
        return json_orders;
    }

    public String getOrder(int id_order) {
        Optional<Order> orderOptional = repository.findById(id_order);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            String order_json = new Gson().toJson(order);
            return order_json;
        } else {
            throw new NoSuchElementException("Order with ID " + id_order + " not found");
        }
    }



    public String addOrder(Order order) {
        repository.save(order);
        String order_json = new Gson().toJson(order);
        return order_json;
    }

    public String getOrderStatus(int id_order) {
        Optional<Order> orderOptional = repository.findById(id_order);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            String status = new Gson().toJson(order.getStatus().toString());
            return status;
        } else {
            throw new NoSuchElementException();
        }
    }

    public String cancelOrder(int id_order) {
        Optional<Order> orderOptional = repository.findById(id_order);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setState(new CancelledState());
            repository.save(order);
            return this.getOrder(id_order);
        } else {
            throw new NoSuchElementException();
        }
    }

    public String addBookToOrder(int orderId, int bookId, int quantity, float price) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order with ID " + orderId + " not found"));
        order.addBook(bookId, quantity, price);
        repository.save(order);
        return new Gson().toJson(order);
    }

    public String decreaseBookInOrder(int orderId, int bookId, int quantity) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order with ID " + orderId + " not found"));
        try {
            order.decreaseBook(bookId, quantity);
            repository.save(order);
            return new Gson().toJson(order);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Book with ID " + bookId + " not found in the order.");
        }
    }


}