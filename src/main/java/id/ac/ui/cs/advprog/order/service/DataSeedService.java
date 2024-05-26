package id.ac.ui.cs.advprog.order.service;

import id.ac.ui.cs.advprog.order.model.Order;
import id.ac.ui.cs.advprog.order.status.WaitingCheckoutState;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class DataSeedService {

    private final OrderService orderService;

    private static final int NUMBER_OF_ORDERS = 1000;

    @Autowired
    public DataSeedService(OrderService orderService) {
        this.orderService = orderService;
    }

    @Async("taskExecutorDefault")
    public CompletableFuture<Boolean> seedOrders() {
        Faker faker = new Faker();

        try {
            for (int i = 0; i < NUMBER_OF_ORDERS; i++) {
                Order order = new Order();
                order.setAddress(faker.address().fullAddress());
                order.setOrderDate(faker.date().toString());
                order.setState(new WaitingCheckoutState());
                order.setTotalPrice(generateTotalPrice());
                order.setCancelable(true);

                orderService.addOrder(order);

                orderService.updateNextStatus(order.getIdOrder());

                orderService.getOrderState(order.getIdOrder());
            }

            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture(false);
        }
    }

    private float generateTotalPrice() {
        return (float) (Math.random() * 1000);
    }
}

