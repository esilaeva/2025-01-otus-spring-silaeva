-- liquibase formatted sql

-- changeset test-user:1
-- comment: Create initial schema for integration tests
CREATE TABLE authors
(
    id        BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL
);

CREATE TABLE genres
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE books
(
    id        BIGSERIAL PRIMARY KEY,
    title     VARCHAR(255) NOT NULL,
    author_id BIGINT       NOT NULL,
    CONSTRAINT fk_books_to_authors FOREIGN KEY (author_id) REFERENCES authors (id) ON DELETE CASCADE
);

CREATE TABLE books_genres
(
    book_id  BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    PRIMARY KEY (book_id, genre_id),
    CONSTRAINT fk_books_genres_to_books FOREIGN KEY (book_id) REFERENCES books (id) ON DELETE CASCADE,
    CONSTRAINT fk_books_genres_to_genres FOREIGN KEY (genre_id) REFERENCES genres (id) ON DELETE CASCADE
);