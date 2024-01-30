package org.bookstore.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "book_id", nullable = false)
    Long bookID;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "author", nullable = false)
    private String author;
    @Column(name = "genre", nullable = false)
    private String genre;
    @Column(name = "price", nullable = false)
    private Double price;
    @Column(name = "quantity_in_stock", nullable = false)
    private Integer quantityInStock;

    //Constructors
    public Book() {

    }
    public Book(String title, String author, String genre, Double price, Integer quantityInStock) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.price = price;
        this.quantityInStock = quantityInStock;
    }

    //Getters and setters

    public Long getBookID() {
        return bookID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(Integer quantityInStock) {
        this.quantityInStock = quantityInStock;
    }
}
