package id.ac.ui.cs.advprog.order;
import id.ac.ui.cs.advprog.order.model.Order;
import id.ac.ui.cs.advprog.order.status.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StatusTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testAllStatusStateAndCancelable() throws Exception {
        Order order = new Order(1);
        assertEquals("Waiting Checkout", order.getStatus());
        assertTrue(order.getCancelable());

        order.nextStatus();
        assertEquals("Waiting Payment", order.getStatus());
        assertTrue(order.getCancelable());

        order.nextStatus();
        assertEquals("Waiting Delivered", order.getStatus());
        assertFalse(order.getCancelable());


        order = new Order(1);
        order.cancelOrder();
        assertEquals("Cancelled", order.getStatus());
        order.nextStatus();
        assertEquals("Cancelled", order.getStatus());
        assertFalse(order.getCancelable());
    }
    }

