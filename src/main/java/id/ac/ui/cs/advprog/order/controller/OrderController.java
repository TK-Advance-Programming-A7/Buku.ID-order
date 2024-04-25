package id.ac.ui.cs.advprog.order.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.ac.ui.cs.advprog.order.model.Order;
import id.ac.ui.cs.advprog.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.HashMap;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("")
    public ResponseEntity<?> getOrder(@RequestBody HashMap<String, Integer> jsonIdOrder) {
        try {
            String orderJson = orderService.getOrder(jsonIdOrder.get("idOrder"));
            return ResponseEntity.ok(orderJson);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no such order.");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<String> addOrder(@RequestBody HashMap<String, Order> requestBody) {
        try {
            Order order = requestBody.get("order");
            String addedOrderJson = orderService.addOrder(order);
            return ResponseEntity.status(HttpStatus.CREATED).body(addedOrderJson);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add order.");
        }
    }


    @PatchMapping("/edit")
    public ResponseEntity<String> editOrder(@RequestBody HashMap<String, Order> requestBody) {
        try {
            Order order = requestBody.get("order");
            String editedOrderJson = orderService.editOrder(order.getIdOrder(), order);
            return ResponseEntity.ok(editedOrderJson);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order with the given ID not found.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to edit order.");
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteOrder(@RequestBody HashMap<String, Integer> jsonIdOrder) {
        try {
            String deletedOrderJson = orderService.deleteOrder(jsonIdOrder.get("idOrder"));
            return ResponseEntity.ok(deletedOrderJson);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order with the given ID not found.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete order.");
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllOrdersOfUser(@RequestBody HashMap<String, Integer> jsonIdUser) {
        try {
            String ordersJson = orderService.getAllOrdersOfUser(jsonIdUser.get("idUser"));
            return ResponseEntity.ok(ordersJson);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch orders.");
        }
    }

    @PostMapping("/book/add")
    public ResponseEntity<String> addBookToOrder(@RequestBody HashMap<String, Object> requestBody) {
        try {
            int orderId = (int) requestBody.get("idOrder");
            int bookId = (int) requestBody.get("idBook");
            int quantity = (int) requestBody.get("quantity");
            float price = Float.parseFloat(requestBody.get("price").toString());

            String addedBookToOrderJson = orderService.addBookToOrder(orderId, bookId, quantity, price);
            return ResponseEntity.ok(addedBookToOrderJson);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order or Book with the given ID not found.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request body.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add book to order.");
        }
    }

    @PatchMapping("/book/decrease")
    public ResponseEntity<String> decreaseBookInOrder(@RequestBody HashMap<String, Object> requestBody) {
        try {
            int orderId = (int) requestBody.get("idOrder");
            int bookId = (int) requestBody.get("idBook");
            int quantity = (int) requestBody.get("quantity");

            String decreasedBookInOrderJson = orderService.decreaseBookInOrder(orderId, bookId, quantity);
            return ResponseEntity.ok(decreasedBookInOrderJson);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order or Book with the given ID not found.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request body.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to decrease book in order.");
        }
    }


}
