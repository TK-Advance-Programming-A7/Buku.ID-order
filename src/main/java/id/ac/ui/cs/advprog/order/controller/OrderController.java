package id.ac.ui.cs.advprog.order.controller;

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
@RequestMapping("")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/api/v1/order")
    public String getOrder(@RequestBody HashMap<String, Integer> json_id_order) {
        String order_json = orderService.getOrder(json_id_order.get("idOrder"));
        return order_json;
    }


}
