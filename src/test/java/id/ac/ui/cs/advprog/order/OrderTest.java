package id.ac.ui.cs.advprog.order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.*;


class OrderTest {
    private List<Book> books;
    private List<Integer> amounts;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        this.books = new ArrayList<>();
        this.amounts = new ArrayList<>();

        // Book 1
        Book book1 = new Book();
        book1.setIdBook(1);
        book1.setTitle("Sampo Cap Bambang");
        book1.setAuthor("Bambang");
        book1.setPublisher("Bambang CV");
        book1.setPrice(10.99f);
        book1.setStock(100);
        book1.setIsbn("1234567890");
        book1.setBookPict("sampo_cap_bambang.jpg");
        book1.setPublishDate(new Date());
        book1.setCategory("Children's Books");
        book1.setPage(50);
        book1.setDescription("A children's book about Sampo Cap Bambang adventures.");
        this.books.add(book1);
        this.amounts.add(2); // Amount of book1

        // Book 2
        Book book2 = new Book();
        book2.setIdBook(2);
        book2.setTitle("The Adventures of Sherlock Holmes");
        book2.setAuthor("Arthur Conan Doyle");
        book2.setPublisher("Penguin Classics");
        book2.setPrice(8.50f);
        book2.setStock(75);
        book2.setIsbn("9780140439070");
        book2.setBookPict("sherlock_holmes.jpg");
        book2.setPublishDate(new Date());
        book2.setCategory("Mystery");
        book2.setPage(320);
        book2.setDescription("A collection of twelve stories featuring Sherlock Holmes, a consulting detective.");
        this.books.add(book2);
        this.amounts.add(1); // Amount of book2
    }

    @Test
    void testCreateOrderEmptyBook() {
        this.books.clear();
        this.amounts.clear();
        assertThrows(IllegalArgumentException.class, () -> {
            Order order = new Order(this.books, this.amounts, 888640678, "Safira Sudrajat");
        });
    }

    @Test
    void testCreateOrderDefaultStatus() {
        Order order = new Order(this.books, this.amounts, 888640678, "Safira Sudrajat");

        assertSame(this.books, order.getBooks());
        assertEquals(2, order.getBooks().size());
        assertEquals("Sampo Cap Bambang", order.getBooks().get(0).getTitle());
        assertEquals("The Adventures of Sherlock Holmes", order.getBooks().get(1).getTitle());
        assertEquals(888640678, order.getIdUser());
        assertEquals("Safira Sudrajat", order.getAddress());
        assertEquals("Waiting Checkout", order.getStatus());
    }

    @Test
    void testSetStatusToWaitingPayment() {
        Order order = new Order(this.books, this.amounts, 888640678, "Safira Sudrajat");
        order.setStatus("Waiting Payment");
        assertEquals("Waiting Payment", order.getStatus());
    }

    @Test
    void testSetStatusToWaitingDelivered() {
        Order order = new Order(this.books, this.amounts, 888640678, "Safira Sudrajat");
        order.setStatus("Waiting Delivered");
        assertEquals("Waiting Delivered", order.getStatus());
    }

    @Test
    void testSetStatusToCancelled() {
        Order order = new Order(this.books, this.amounts, 888640678, "Safira Sudrajat");
        order.setStatus("Cancelled");
        assertEquals("Cancelled", order.getStatus());
    }

    @Test
    void testSetStatusToInvalidStatus() {
        Order order = new Order(this.books, this.amounts, 888640678, "Safira Sudrajat");
        assertThrows(IllegalArgumentException.class, () -> order.setStatus("MEOW"));
    }
}
