--liquibase formatted sql

--changeset data_authors:data-1
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM authors
INSERT INTO authors (id, full_name)
VALUES (1, 'Author_1');
INSERT INTO authors (id, full_name)
VALUES (2, 'Author_2');
INSERT INTO authors (id, full_name)
VALUES (3, 'Author_3');
--rollback DELETE FROM authors WHERE id IN (1,2,3);

--changeset data_genres:data-2
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM genres
INSERT INTO genres (id, name)
VALUES (1, 'Genre_1');
INSERT INTO genres (id, name)
VALUES (2, 'Genre_2');
INSERT INTO genres (id, name)
VALUES (3, 'Genre_3');
INSERT INTO genres (id, name)
VALUES (4, 'Genre_4');
INSERT INTO genres (id, name)
VALUES (5, 'Genre_5');
INSERT INTO genres (id, name)
VALUES (6, 'Genre_6');
--rollback DELETE FROM genres WHERE id BETWEEN 1 AND 6;

--changeset data_books:data-3
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM books
INSERT INTO books (id, title, author_id)
VALUES (1, 'BookTitle_1', 1);
INSERT INTO books (id, title, author_id)
VALUES (2, 'BookTitle_2', 2);
INSERT INTO books (id, title, author_id)
VALUES (3, 'BookTitle_3', 3);
INSERT INTO books (id, title, author_id)
VALUES (4, 'BookTitle_4', 1);
INSERT INTO books (id, title, author_id)
VALUES (5, 'BookTitle_5', 2);
INSERT INTO books (id, title, author_id)
VALUES (6, 'BookTitle_6', 3);
--rollback DELETE FROM books WHERE id BETWEEN 1 AND 6;

--changeset data_books_genres:data-4
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM books_genres
INSERT INTO books_genres (book_id, genre_id)
VALUES (1, 1);
INSERT INTO books_genres (book_id, genre_id)
VALUES (1, 2);
INSERT INTO books_genres (book_id, genre_id)
VALUES (2, 3);
INSERT INTO books_genres (book_id, genre_id)
VALUES (2, 4);
INSERT INTO books_genres (book_id, genre_id)
VALUES (3, 5);
INSERT INTO books_genres (book_id, genre_id)
VALUES (3, 6);
INSERT INTO books_genres (book_id, genre_id)
VALUES (4, 1);
INSERT INTO books_genres (book_id, genre_id)
VALUES (4, 2);
INSERT INTO books_genres (book_id, genre_id)
VALUES (5, 3);
INSERT INTO books_genres (book_id, genre_id)
VALUES (5, 4);
INSERT INTO books_genres (book_id, genre_id)
VALUES (6, 5);
INSERT INTO books_genres (book_id, genre_id)
VALUES (6, 6);
--rollback DELETE FROM books_genres WHERE (book_id BETWEEN 1 AND 6) AND (genre_id BETWEEN 1 AND 6);