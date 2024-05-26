package id.ac.ui.cs.advprog.order.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.ac.ui.cs.advprog.order.model.Order;
import id.ac.ui.cs.advprog.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@CrossOrigin(origins = "http://localhost:3000")

@RequestMapping("/api/v1/order")
public class OrderController {

    private static final String ID_ORDER = "idOrder";

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{idOrder}")
    public ResponseEntity<String> getOrder(@PathVariable int idOrder) {
        try {
            String orderJson = orderService.getOrder(idOrder);
            return ResponseEntity.ok(orderJson);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found.");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/add")
    public CompletableFuture<ResponseEntity<String>> addOrder(@RequestBody Map<String, Order> requestBody) {
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
    public CompletableFuture<ResponseEntity<String>> editOrder(@RequestBody Map<String, Order> requestBody) {
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
    public CompletableFuture<ResponseEntity<String>> deleteOrder(@RequestBody Map<String, Integer> jsonIdOrder) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String deletedOrderJson = orderService.deleteOrder(jsonIdOrder.get(ID_ORDER));
                return ResponseEntity.ok(deletedOrderJson);
            } catch (NoSuchElementException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order with the given ID not found.");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete order.");
            }
        });
    }

    @GetMapping("/users/{idUser}")
    public ResponseEntity<String> getAllOrdersOfUser(@PathVariable String idUser) {
        try {
            String ordersJson = orderService.getAllOrdersOfUser(idUser);
            return ResponseEntity.ok(ordersJson);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch orders.");
        }
    }

    @PostMapping("/book/add")
    public CompletableFuture<ResponseEntity<String>> addBookToOrder(@RequestBody Map<String, Object> requestBody) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                int orderId = (int) requestBody.get(ID_ORDER);
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
    public CompletableFuture<ResponseEntity<String>> decreaseBookInOrder(@RequestBody Map<String, Object> requestBody) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                int orderId = (int) requestBody.get(ID_ORDER);
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
    public CompletableFuture<ResponseEntity<String>> nextStatus(@RequestBody Map<String, Integer> jsonIdOrder) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String updatedOrder = orderService.updateNextStatus(jsonIdOrder.get(ID_ORDER));
                return ResponseEntity.ok(updatedOrder);
            } catch (NoSuchElementException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no such order.");
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @PatchMapping("/cancel")
    public CompletableFuture<ResponseEntity<String>> cancelOrder(@RequestBody Map<String, Integer> jsonIdOrder) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String cancelOrder = orderService.cancelOrder(jsonIdOrder.get(ID_ORDER));
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

    @GetMapping("users/status")
    public ResponseEntity<String> getOrderByUserIdAndStatus(@RequestParam String userId, @RequestParam String status) {
        try {
            String ordersJson = orderService.getOrdersByUserIdAndStatus(userId, status);
            return ResponseEntity.ok(ordersJson);
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body("User ID or status not provided.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to fetch orders.");
        }
    }

    @DeleteMapping("/book/delete")
    public CompletableFuture<ResponseEntity<String>> deleteItemFromOrder(@RequestBody Map<String, Object> requestBody) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                int orderId = (int) requestBody.get(ID_ORDER);
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