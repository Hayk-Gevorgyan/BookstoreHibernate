package org.bookstore.management;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.bookstore.entities.*;
import org.bookstore.hibernate.HibernateConfig;
import org.hibernate.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Bookstore {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        SessionFactory sessionFactory = HibernateConfig.getSessionFactory();
        Session session = sessionFactory.openSession();

        boolean fin = false;
        Scanner commandInput = new Scanner(System.in);

        try{
            while (!fin) {
                System.out.println("""
                        Commands
                        1 - update book info
                        2 - update customer info
                        3 - list books by genre
                        4 - list books by author
                        5 - list customer purchase history
                        6 - list sales history
                        7 - list total revenue by genre
                        8 - finish
                        input : """);
                String command = commandInput.nextLine();
                switch (command) {
                    case "1" -> updateBookDetails(session);
                    case "2" -> updateCustomerDetails(session);
                    case "3" -> listBooksByGenre(session);
                    case "4" -> listBooksByAuthor(session);
                    case "5" -> listCustomerPurchaseHistory(session);
                    case "6" -> listSaleHistory(session);
                    case "7" -> generateTotalRevenueByGenre(session);
                    case "8" -> {
                        fin = true;
                        HibernateConfig.finish();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void updateBookDetails(Session session) throws SQLException, NumberFormatException {
        Transaction transaction = null;

        try {
            System.out.println("Enter book ID:");
            Long bookID = scanner.nextLong();
            scanner.nextLine();

            System.out.println("Enter new title if you want to change it:");
            String newTitle = scanner.nextLine();
            System.out.println("Enter new author name if you want to change it:");
            String newAuthor = scanner.nextLine();
            System.out.println("Enter new genre name if you want to change it:");
            String newGenre = scanner.nextLine();
            System.out.println("Enter new price or -1 if you don't want to change it:");
            double newPrice = scanner.nextDouble();
            scanner.nextLine();
            System.out.println("Enter new quantity in stock or -1 if you don't want to change it:");
            int newQuantity = scanner.nextInt();
            scanner.nextLine();

            transaction = session.beginTransaction();

            Book book = session.get(Book.class, bookID);

            if (book != null) {
                if (!newTitle.trim().isEmpty()) {
                    book.setTitle(newTitle);
                }
                if (!newAuthor.trim().isEmpty()) {
                    book.setAuthor(newAuthor);
                }
                if (!newGenre.trim().isEmpty()) {
                    book.setGenre(newGenre);
                }
                if (newPrice != -1) {
                    book.setPrice(newPrice);
                }
                if (newQuantity != -1) {
                    book.setQuantityInStock(newQuantity);
                }

                session.persist(book);
                session.flush();
                transaction.commit();
            } else {
                System.out.println("Book with ID " + bookID + " not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input format. Please enter valid numeric values.");
        } finally {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        }
    }
    private static void updateCustomerDetails(Session session) throws SQLException, NumberFormatException {
        Transaction transaction = null;

        try {
            System.out.println("Enter customer ID:");
            Long customerID = scanner.nextLong();
            scanner.nextLine();

            System.out.println("Enter new name if you want to change it:");
            String newName = scanner.nextLine();
            System.out.println("Enter new email if you want to change it:");
            String newEmail = scanner.nextLine();
            System.out.println("Enter new phone number if you want to change it:");
            String newPhone = scanner.nextLine();

            transaction = session.beginTransaction();

            Customer customer = session.get(Customer.class, customerID);

            if (customer != null) {
                if (!newName.trim().isEmpty()) {
                    customer.setName(newName);
                }
                if (!newEmail.trim().isEmpty()) {
                    customer.setEmail(newEmail);
                }
                if (!newPhone.trim().isEmpty()) {
                    customer.setPhone(newPhone);
                }

                session.persist(customer);
                session.flush();
                transaction.commit();
            } else {
                System.out.println("Customer with ID " + customerID + " not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input format. Please enter valid numeric values.");
        } finally {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        }
    }
    private static void listBooksByGenre(Session session) throws SQLException {
        System.out.print("Enter genre: ");
        String genre = scanner.nextLine().trim();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Book> criteriaQuery = criteriaBuilder.createQuery(Book.class);
        Root<Book> root = criteriaQuery.from(Book.class);

        criteriaQuery.multiselect(
                root.get("title"),
                root.get("author"),
                root.get("genre"),
                root.get("price"),
                root.get("quantityInStock")
        );

        criteriaQuery.where(criteriaBuilder.equal(root.get("genre"), genre));

        List<Book> resultList = session.createQuery(criteriaQuery).getResultList();

        if (resultList.isEmpty()) {
            System.out.println("No books found for the given genre.");
        } else {
            for (Book row : resultList) {
                String title = row.getTitle();
                String author = row.getAuthor();
                Double price = row.getPrice();
                int quantityInStock = row.getQuantityInStock();

                System.out.printf("Title: %s, Author: %s, Price: %.2f, QuantityInStock: %d%n",
                        title, author, price, quantityInStock);
            }
        }
    }
    private static void listBooksByAuthor(Session session) throws SQLException {
        System.out.print("Enter author: ");
        String author = scanner.nextLine().trim();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Book> criteriaQuery = criteriaBuilder.createQuery(Book.class);
        Root<Book> root = criteriaQuery.from(Book.class);

        criteriaQuery.multiselect(
                root.get("title"),
                root.get("author"),
                root.get("genre"),
                root.get("price"),
                root.get("quantityInStock")
        );

        criteriaQuery.where(criteriaBuilder.equal(root.get("author"), author));

        List<Book> resultList = session.createQuery(criteriaQuery).getResultList();

        if (resultList.isEmpty()) {
            System.out.println("No books found for the given author.");
        } else {
            for (Book row : resultList) {
                String title = row.getTitle();
                String genre = row.getGenre();
                Double price = row.getPrice();
                int quantityInStock = row.getQuantityInStock();

                System.out.printf("Title: %s, Genre: %s, Price: %.2f, QuantityInStock: %d%n",
                        title, genre, price, quantityInStock);
            }
        }
    }
    private static void listCustomerPurchaseHistory(Session session) throws SQLException {
        System.out.print("Enter customer ID: ");
        long customerID = scanner.nextLong();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> criteriaQuery = criteriaBuilder.createQuery(Object[].class);
        Root<Customer> customerRoot = criteriaQuery.from(Customer.class);
        Root<Sale> saleRoot = criteriaQuery.from(Sale.class);
        Root<Book> bookRoot = criteriaQuery.from(Book.class);

        criteriaQuery.multiselect(
                customerRoot.get("name").alias("CustomerName"),
                bookRoot.get("title"),
                bookRoot.get("author"),
                bookRoot.get("genre"),
                saleRoot.get("dateOfSale")
        );

        criteriaQuery.where(
                criteriaBuilder.equal(customerRoot.get("customerID"), saleRoot.get("customer").get("customerID")),
                criteriaBuilder.equal(bookRoot.get("bookID"), saleRoot.get("book").get("bookID")),
                criteriaBuilder.equal(customerRoot.get("customerID"), customerID)
        );

        List<Object[]> resultList = session.createQuery(criteriaQuery).getResultList();

        if (resultList.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        for (Object[] row : resultList) {
            String customerName = (String) row[0];
            String title = (String) row[1];
            String author = (String) row[2];
            String genre = (String) row[3];
            LocalDate dateOfSale = (LocalDate) row[4];

            System.out.printf("Customer: %s, Title: %s, Author: %s, Genre: %s, Date of Sale: %s%n",
                    customerName, title, author, genre, dateOfSale);
        }
    }
    private static void listSaleHistory(Session session) throws SQLException {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> criteriaQuery = criteriaBuilder.createQuery(Object[].class);
        Root<Customer> customerRoot = criteriaQuery.from(Customer.class);
        Root<Sale> saleRoot = criteriaQuery.from(Sale.class);
        Root<Book> bookRoot = criteriaQuery.from(Book.class);

        criteriaQuery.multiselect(
                customerRoot.get("name").alias("CustomerName"),
                bookRoot.get("title"),
                saleRoot.get("dateOfSale")
        );

        criteriaQuery.where(
                criteriaBuilder.equal(customerRoot.get("customerID"), saleRoot.get("customer").get("customerID")),
                criteriaBuilder.equal(bookRoot.get("bookID"), saleRoot.get("book").get("bookID"))
        );

        List<Object[]> resultList = session.createQuery(criteriaQuery).getResultList();

        if (resultList.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        for (Object[] row : resultList) {
            String customerName = (String) row[0];
            String bookTitle = (String) row[1];
            LocalDate dateOfSale = (LocalDate) row[2];

            System.out.printf("Customer: %s, Book Title: %s, Date of Sale: %s%n",
                    customerName, bookTitle, dateOfSale);
        }
    }
    private static void generateTotalRevenueByGenre(Session session) throws SQLException {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> criteriaQuery = criteriaBuilder.createQuery(Object[].class);
        Root<Sale> saleRoot = criteriaQuery.from(Sale.class);
        Root<Book> bookRoot = criteriaQuery.from(Book.class);

        criteriaQuery.multiselect(
                bookRoot.get("genre"),
                criteriaBuilder.sum(saleRoot.get("totalPrice"))
        );

        criteriaQuery.where(
                criteriaBuilder.equal(saleRoot.get("book"), bookRoot)
        );

        criteriaQuery.groupBy(bookRoot.get("genre"));

        List<Object[]> resultList = session.createQuery(criteriaQuery).getResultList();

        if (resultList.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        for (Object[] row : resultList) {
            String genre = (String) row[0];
            double totalRevenue = (Double) row[1];

            System.out.printf("Genre: %s, Total Revenue: %.2f%n", genre, totalRevenue);
        }
    }
}
