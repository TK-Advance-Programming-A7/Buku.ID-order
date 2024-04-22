package id.ac.ui.cs.advprog.order.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

// Dummy class for testing


@Setter @Getter
public class Book {
    private int idBook;
    private String title;
    private String author;
    private String publisher;
    private float price;
    private int stock;
    private String isbn;
    private String bookPict;
    private Date publishDate;
    private String category;
    private int page;
    private String description;

    public Book(int idBook, String title, String author, String publisher, float price, int stock, String isbn, String bookPict, Date publishDate, String category, int page, String description) {
        this.idBook = idBook;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.price = price;
        this.stock = stock;
        this.isbn = isbn;
        this.bookPict = bookPict;
        this.publishDate = publishDate;
        this.category = category;
        this.page = page;
        this.description = description;
    }

    // getters and setters
    // ...
}