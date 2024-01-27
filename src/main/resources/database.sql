CREATE TABLE customers
(
    customer_id BIGSERIAL PRIMARY KEY ,
    name VARCHAR(50) NOT NULL ,
    email VARCHAR(50) NOT NULL CONSTRAINT unique_email UNIQUE ,
    phone VARCHAR(20) NOT NULL CONSTRAINT unique_phone UNIQUE
);

CREATE TABLE books
(
    book_id BIGSERIAL PRIMARY KEY ,
    title VARCHAR(50) NOT NULL CONSTRAINT unique_title UNIQUE ,
    author VARCHAR(50) NOT NULL ,
    genre VARCHAR(50) NOT NULL ,
    price NUMERIC(10, 2) NOT NULL ,
    quantity_in_stock INTEGER NOT NULL
);

CREATE FUNCTION calculate_total_price(bookid BIGINT, quantity_sold INTEGER)
    RETURNS NUMERIC AS $$
DECLARE
    book_price NUMERIC;
    total_price NUMERIC;
BEGIN
    SELECT price INTO book_price FROM books WHERE book_id = bookid;
    total_price := book_price * quantity_sold;
    RETURN total_price;
END;
$$ LANGUAGE plpgsql;

CREATE TABLE sales
(
    sale_id BIGSERIAL PRIMARY KEY ,
    book_id BIGINT NOT NULL REFERENCES books,
    customer_id BIGINT NOT NULL REFERENCES customers,
    date_of_sale DATE NOT NULL ,
    quantity_sold INTEGER NOT NULL ,
    total_price NUMERIC(10, 2) DEFAULT 0,
    CONSTRAINT check_total_price
        CHECK (total_price = calculate_total_price(book_id, quantity_sold))
);


CREATE FUNCTION update_quantity_in_stock()
    RETURNS TRIGGER AS $$
BEGIN
    UPDATE books
    SET quantity_in_stock = quantity_in_stock - NEW.quantity_sold
    WHERE book_id = NEW.book_id;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE FUNCTION set_total_price()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.total_price := calculate_total_price(NEW.book_id, NEW.quantity_sold);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_quantity_in_stock_trigger
    AFTER INSERT ON sales
    FOR EACH ROW
EXECUTE FUNCTION update_quantity_in_stock();

CREATE TRIGGER set_total_price_trigger
    BEFORE INSERT ON sales
    FOR EACH ROW
EXECUTE FUNCTION set_total_price();

INSERT INTO Books (title, author, genre, price, quantity_in_stock)
VALUES
    ('To Kill a Mockingbird', 'Harper Lee', 'Fiction', 15.99, 25),
    ('1984', 'George Orwell', 'Science Fiction', 12.49, 20),
    ('Pride and Prejudice', 'Jane Austen', 'Romance', 10.99, 30),
    ('The Great Gatsby', 'F. Scott Fitzgerald', 'Classic', 11.99, 18),
    ('Harry Potter and the Sorcerer''s Stone', 'J.K. Rowling', 'Fantasy', 18.75, 22),
    ('The Catcher in the Rye', 'J.D. Salinger', 'Coming-of-Age', 9.99, 15),
    ('The Hobbit', 'J.R.R. Tolkien', 'Fantasy', 14.25, 28),
    ('To the Lighthouse', 'Virginia Woolf', 'Modernist', 13.45, 12),
    ('The Lord of the Rings', 'J.R.R. Tolkien', 'Fantasy', 24.99, 20),
    ('Moby Dick', 'Herman Melville', 'Adventure', 16.99, 17);


INSERT INTO customers(name, email, phone)
VALUES
    ('Rocket','rocketnotracoon@galaxy.org','111111111111'),
    ('Peter Quill','starlord@galaxy.org','+999-475-3691-280'),
    ('Thanos','saviour@galaxy.org','+999-123-4567-890'),
    ('Iron Man','mrbest@stark.com','+1-000-0000-000'),
    ('Groot','groot@galaxy.org','');


INSERT INTO sales (book_id, customer_id, date_of_sale, quantity_sold)
VALUES
    (1, 1, '2023-01-05', 2),
    (3, 2, '2023-02-10', 1),
    (5, 3, '2023-03-15', 3),
    (7, 4, '2023-04-20', 1),
    (9, 5, '2023-05-25', 2),
    (2, 1, '2023-06-05', 1),
    (4, 2, '2023-07-10', 2),
    (6, 3, '2023-08-15', 1),
    (8, 4, '2023-09-20', 3),
    (10, 5, '2023-10-25', 2);

