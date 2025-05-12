package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookController {

    private static final String REDIRECT_TO_MAIN = "redirect:/books";

    private final AuthorService authorService;

    private final BookService bookService;

    private final GenreService genreService;


    @GetMapping("/books")
    public String allBooksPage(Model model) {
        List<BookDto> books = bookService.findAll();
        List<AuthorDto> authors = authorService.findAll();
        List<GenreDto> genres = genreService.findAll();


        model.addAttribute("books", books);
        model.addAttribute("authors", authors);
        model.addAttribute("genres", genres);

        return "books";
    }

    @PostMapping("/book/add")
    public String insertBook(String title, long authorId, long genreId) {
        bookService.create(title, authorId, genreId);

        return REDIRECT_TO_MAIN;
    }

    /*
    The filter that handles the _method request parameter is now disabled by default as
    it causes early consumption of a request body if the body may contain parameters.
    This can be restored by setting either spring.webflux.hiddenmethod.filter.enabled
    or spring.mvc.hiddenmethod.filter.enabled to true.
    */
    @DeleteMapping("/book/{id}")
    public String deleteBook(@PathVariable long id) {
        bookService.deleteById(id);

        return REDIRECT_TO_MAIN;

    }

    @PatchMapping("/book/{id}")
    public String showEditPage(@PathVariable long id, Model model) {
        BookDto book = bookService.findById(id);
        List<AuthorDto> authors = authorService.findAll();
        List<GenreDto> genres = genreService.findAll();

        model.addAttribute("book", book);
        model.addAttribute("authors", authors);
        model.addAttribute("genres", genres);

        return "book-edit";
    }

    @PostMapping("/book/{id}")
    public String updateBook(@PathVariable long id, String title, long authorId, long genreId) {
        bookService.update(id, title, authorId, genreId);

        return REDIRECT_TO_MAIN;
    }
}
