package id.ac.ui.cs.advprog.order.service;

import id.ac.ui.cs.advprog.order.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DataSeedServiceTest {

    @Mock
    private OrderService orderService;

    private DataSeedService dataSeedService;

    private static int NUMBER_OF_ORDERS = 1000;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        dataSeedService = new DataSeedService(orderService);
    }

    @Test
    public void testSeedOrders_Success() throws Exception {
        // Arrange
        String dummyOrderJson = "{\"id\":1,\"address\":\"123 Main St\",\"orderDate\":\"2024-05-26\",\"state\":\"Waiting Checkout\",\"totalPrice\":500.0,\"cancelable\":true}";
        when(orderService.addOrder(any(Order.class))).thenReturn(dummyOrderJson);
        when(orderService.updateNextStatus(anyInt())).thenReturn(dummyOrderJson);
        when(orderService.getOrderState(anyInt())).thenReturn("Waiting Checkout");

        // Act
        CompletableFuture<Boolean> resultFuture = dataSeedService.seedOrders();
        boolean result = resultFuture.get();

        // Assert
        assertTrue(result);
        verify(orderService, times(NUMBER_OF_ORDERS)).addOrder(any(Order.class));
        verify(orderService, times(NUMBER_OF_ORDERS)).updateNextStatus(anyInt());
        verify(orderService, times(NUMBER_OF_ORDERS)).getOrderState(anyInt());

        // Additional assertions to verify the data if needed
        verify(orderService, times(NUMBER_OF_ORDERS)).addOrder(orderCaptor.capture());
        for (Order capturedOrder : orderCaptor.getAllValues()) {
            assertEquals("Waiting Checkout", capturedOrder.getState().toString());
            assertTrue(capturedOrder.getTotalPrice() >= 0 && capturedOrder.getTotalPrice() <= 1000);
            assertTrue(capturedOrder.isCancelable());
        }
    }

    @Test
    public void testSeedOrders_Failure() throws Exception {
        // Arrange
        when(orderService.addOrder(any(Order.class))).thenThrow(new RuntimeException("Failed to add order"));

        // Act
        CompletableFuture<Boolean> resultFuture = dataSeedService.seedOrders();
        boolean result = resultFuture.get();

        // Assert
        assertFalse(result);
        verify(orderService, atLeastOnce()).addOrder(any(Order.class));
    }
}
