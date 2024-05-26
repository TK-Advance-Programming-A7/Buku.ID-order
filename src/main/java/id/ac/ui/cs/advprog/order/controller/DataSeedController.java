package id.ac.ui.cs.advprog.order.controller;

import id.ac.ui.cs.advprog.order.service.DataSeedService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/order/data-seed")
public class DataSeedController {

    private final DataSeedService dataSeedService;

    public DataSeedController(DataSeedService dataSeedService) {
        this.dataSeedService = dataSeedService;
    }

    @GetMapping("/seed-orders")
    public ResponseEntity<String> seedOrders() throws ExecutionException, InterruptedException {
        CompletableFuture<Boolean> seedResult = dataSeedService.seedOrders();
        boolean result = seedResult.get();

        if (result) {
            return ResponseEntity.status(HttpStatus.OK).body("Data pesanan berhasil disimpan!");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Gagal menyimpan data pesanan.");
        }
    }
}

