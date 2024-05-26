package id.ac.ui.cs.advprog.order.controller;

import id.ac.ui.cs.advprog.order.service.DataSeedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

class DataSeedControllerTest {

    @Mock
    private DataSeedService dataSeedService;

    private DataSeedController dataSeedController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dataSeedController = new DataSeedController(dataSeedService);
    }

    @Test
    void seedOrders_Success() throws ExecutionException, InterruptedException {
        // Given
        CompletableFuture<Boolean> successfulResult = CompletableFuture.completedFuture(true);
        when(dataSeedService.seedOrders()).thenReturn(successfulResult);

        // When
        ResponseEntity<String> responseEntity = dataSeedController.seedOrders();

        // Then
        verify(dataSeedService, times(1)).seedOrders();
        assert responseEntity.getStatusCode().equals(HttpStatus.OK);
        assert responseEntity.getBody().equals("Data pesanan berhasil disimpan!");
    }

    @Test
    void seedOrders_Failure() throws ExecutionException, InterruptedException {
        // Given
        CompletableFuture<Boolean> failedResult = CompletableFuture.completedFuture(false);
        when(dataSeedService.seedOrders()).thenReturn(failedResult);

        // When
        ResponseEntity<String> responseEntity = dataSeedController.seedOrders();

        // Then
        verify(dataSeedService, times(1)).seedOrders();
        assert responseEntity.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR);
        assert responseEntity.getBody().equals("Gagal menyimpan data pesanan.");
    }
}
