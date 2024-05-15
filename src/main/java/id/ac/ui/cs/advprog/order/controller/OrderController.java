package id.ac.ui.cs.advprog.order.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.ac.ui.cs.advprog.order.model.Order;
import id.ac.ui.cs.advprog.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

import java.util.HashMap;
import java.util.NoSuchElementException;

@RestController
@CrossOrigin(origins = "http://localhost:3000")

@RequestMapping("/api/v1/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("")
    public CompletableFuture<ResponseEntity<?>> getOrder(@RequestBody HashMap<String, Integer> jsonIdOrder) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String orderJson = orderService.getOrder(jsonIdOrder.get("idOrder"));
                return ResponseEntity.ok(orderJson);
            } catch (NoSuchElementException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no such order.");
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }



    @PostMapping("/add")
    public CompletableFuture<ResponseEntity<?>> addOrder(@RequestBody HashMap<String, Order> requestBody) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Order order = requestBody.get("order");
                String addedOrderJson = orderService.addOrder(order);
                return ResponseEntity.status(HttpStatus.CREATED).body(addedOrderJson);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add order.");
            }
        });
    }

    @PatchMapping("/edit")
    public CompletableFuture<ResponseEntity<?>> editOrder(@RequestBody HashMap<String, Order> requestBody) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Order order = requestBody.get("order");
                String editedOrderJson = orderService.editOrder(order.getIdOrder(), order);
                return ResponseEntity.ok(editedOrderJson);
            } catch (NoSuchElementException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order with the given ID not found.");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to edit order.");
            }
        });
    }

    @DeleteMapping("/delete")
    public CompletableFuture<ResponseEntity<?>> deleteOrder(@RequestBody HashMap<String, Integer> jsonIdOrder) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String deletedOrderJson = orderService.deleteOrder(jsonIdOrder.get("idOrder"));
                return ResponseEntity.ok(deletedOrderJson);
            } catch (NoSuchElementException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order with the given ID not found.");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete order.");
            }
        });
    }

    @GetMapping("/users")
    public CompletableFuture<ResponseEntity<?>> getAllOrdersOfUser(@RequestBody HashMap<String, Integer> jsonIdUser) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String ordersJson = orderService.getAllOrdersOfUser(jsonIdUser.get("idUser"));
                return ResponseEntity.ok(ordersJson);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch orders.");
            }
        });
    }

    @GetMapping("/users/status")
    public CompletableFuture<ResponseEntity<?>> getAllOrdersOfUserStatus(@RequestBody HashMap<String, Object> requestBody) {
        return CompletableFuture.supplyAsync(() -> {
            try {

                int idUser = (int) requestBody.get("idUser");
                String status = (String) requestBody.get("status");

                String ordersJson = orderService.getAllOrdersOfUserByStatus(idUser, status);
                return ResponseEntity.ok(ordersJson);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch orders.");
            }
        });
    }

    @PostMapping("/book/add")
    public CompletableFuture<ResponseEntity<?>> addBookToOrder(@RequestBody HashMap<String, Object> requestBody) {
        return CompletableFuture.supplyAsync(() -> {
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
        });
    }

    @PatchMapping("/book/decrease")
    public CompletableFuture<ResponseEntity<?>> decreaseBookInOrder(@RequestBody HashMap<String, Object> requestBody) {
        return CompletableFuture.supplyAsync(() -> {
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
        });
    }

    @PatchMapping("/next")
    public CompletableFuture<ResponseEntity<?>> nextStatus(@RequestBody HashMap<String, Integer> jsonIdOrder) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String updatedOrder = orderService.updateNextStatus(jsonIdOrder.get("idOrder"));
                return ResponseEntity.ok(updatedOrder);
            } catch (NoSuchElementException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no such order.");
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @PatchMapping("/cancel")
    public CompletableFuture<ResponseEntity<?>> cancelOrder(@RequestBody HashMap<String, Integer> jsonIdOrder) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String cancelOrder = orderService.cancelOrder(jsonIdOrder.get("idOrder"));
                return ResponseEntity.ok(cancelOrder);
            } catch (NoSuchElementException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no such order.");
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order is not cancelable.");
            }
        });
    }

    @PostMapping("/users/status")
    public CompletableFuture<ResponseEntity<?>> getOrderByUserIdAndStatus(@RequestBody HashMap<String, Object> requestBody) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                int userId = (int) requestBody.get("idUser");
                String status = (String) requestBody.get("status");
                String ordersJson = orderService.getOrdersByUserIdAndStatus(userId, status);
                return ResponseEntity.ok(ordersJson);
            } catch (NoSuchElementException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User ID or status not provided.");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch orders.");
            }
        });
    }

    @DeleteMapping("/book/delete")
    public CompletableFuture<ResponseEntity<?>> deleteItemFromOrder(@RequestBody HashMap<String, Object> requestBody) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                int orderId = (int) requestBody.get("idOrder");
                int itemId = (int) requestBody.get("idOrderItem");

                String deletedItemFromOrderJson = orderService.deleteItemFromOrder(orderId, itemId);
                return ResponseEntity.ok(deletedItemFromOrderJson);
            } catch (NoSuchElementException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order or Item with the given ID not found.");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete item from order.");
            }
        });
    }


}