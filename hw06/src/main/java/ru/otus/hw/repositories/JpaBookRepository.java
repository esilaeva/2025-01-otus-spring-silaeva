package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@Repository
@RequiredArgsConstructor
public class JpaBookRepository implements BookRepository {
    
    public static final String BOOK_NOT_FOUND = "Book with id: %d not found";
    
    @PersistenceContext
    private final EntityManager em;
    
    @Override
    public Optional<Book> findById(long id) {
        var entityGraph = em.getEntityGraph("book:author-genre-entity-graph");
        return Optional.ofNullable(em.find(Book.class, id, Map.of(FETCH.getKey(), entityGraph)));
    }
    
    @Override
    public List<Book> findAll() {
        var entityGraph = em.getEntityGraph("book:author-genre-entity-graph");
        return em.createQuery("SELECT b FROM Book b", Book.class)
            .setHint(FETCH.getKey(), entityGraph)
            .getResultList();
    }
    
    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            em.persist(book);
            return book;
        } else {
            return em.merge(book);
        }
    }
    
    @Override
    public void deleteById(long id) {
        Book book = em.find(Book.class, id);
        if (book != null) {
            em.remove(book);
        }
    }
}

