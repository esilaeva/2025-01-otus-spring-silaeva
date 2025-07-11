--liquibase formatted sql

--changeset table_author:1
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM pg_tables WHERE tablename = 'authors'
CREATE TABLE authors
(
    id        BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL
);

--changeset table_genres:2
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM pg_tables WHERE tablename = 'genres'
CREATE TABLE genres
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT uq_genre_name UNIQUE (name)
);

--changeset table_books:3
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM pg_tables WHERE tablename = 'books'
CREATE TABLE books
(
    id        BIGSERIAL PRIMARY KEY,
    title     VARCHAR(255) NOT NULL,
    author_id BIGINT       NOT NULL,
    CONSTRAINT fk_book_author_id FOREIGN KEY (author_id) REFERENCES authors (id)
);

--changeset table_boos_genres:4
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM pg_tables WHERE tablename = 'books_genres'
CREATE TABLE books_genres
(
    book_id  BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    CONSTRAINT fk_book_genre_book_id FOREIGN KEY (book_id) REFERENCES books (id),
    CONSTRAINT fk_book_genre_genre_id FOREIGN KEY (genre_id) REFERENCES genres (id)
);

ALTER TABLE books_genres
    ADD CONSTRAINT pk_books_genres PRIMARY KEY (book_id, genre_id);