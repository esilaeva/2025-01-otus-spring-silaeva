package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.mapper.DtoToDtoMapper;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookController {

    private static final String REDIRECT_TO_MAIN_PAGE = "redirect:/books";

    private static final String ATTRIBUTE_BOOK = "book";

    private static final String ATTRIBUTE_AUTHORS = "authors";

    private static final String ATTRIBUTE_GENRES = "genres";

    private final AuthorService authorService;

    private final BookService bookService;

    private final GenreService genreService;

    private final DtoToDtoMapper dtoToDtoMapper;


    @GetMapping("/books")
    public String allBooksPage(Model model) {
        List<BookDto> books = bookService.findAll();
        model.addAttribute("books", books);
        return "books";
    }

    @GetMapping("/book/{id}")
    public String bookEditPage(@PathVariable long id, Model model) {
        BookDto book = bookService.findById(id);
        List<AuthorDto> authors = authorService.findAll();
        List<GenreDto> genres = genreService.findAll();

        model.addAttribute(ATTRIBUTE_BOOK, dtoToDtoMapper.bookDtoToBookUpdateDto(book));
        model.addAttribute(ATTRIBUTE_AUTHORS, authors);
        model.addAttribute(ATTRIBUTE_GENRES, genres);

        return "book-edit";
    }

    @GetMapping("/book")
    public String bookCreatePage(Model model) {

        List<AuthorDto> authors = authorService.findAll();
        List<GenreDto> genres = genreService.findAll();

        model.addAttribute(ATTRIBUTE_AUTHORS, authors);
        model.addAttribute(ATTRIBUTE_GENRES, genres);

        return "book-create";
    }

    @PutMapping("/book")
    public String updateBook(@Validated @ModelAttribute(ATTRIBUTE_BOOK) BookUpdateDto bookUpdateDto,
                             BindingResult bindingResult,
                             Model model) {

        if (bindingResult.hasErrors()) {
            List<AuthorDto> authors = authorService.findAll();
            List<GenreDto> genres = genreService.findAll();

            model.addAttribute(ATTRIBUTE_AUTHORS, authors);
            model.addAttribute(ATTRIBUTE_GENRES, genres);

            return "book-edit";
        }

        bookService.update(bookUpdateDto);

        return REDIRECT_TO_MAIN_PAGE;
    }

    @PostMapping("/book")
    public String insertBook(@Validated @ModelAttribute(ATTRIBUTE_BOOK) BookCreateDto bookCreateDto,
                             BindingResult bindingResult,
                             Model model) {

        if (bindingResult.hasErrors()) {

            List<AuthorDto> authors = authorService.findAll();
            List<GenreDto> genres = genreService.findAll();
            model.addAttribute(ATTRIBUTE_BOOK, bookCreateDto);
            model.addAttribute(ATTRIBUTE_AUTHORS, authors);
            model.addAttribute(ATTRIBUTE_GENRES, genres);

            return "book-create";
        }

        bookService.create(bookCreateDto);

        return REDIRECT_TO_MAIN_PAGE;
    }

    @DeleteMapping("/book/{id}")
    public String deleteBook(@PathVariable long id) {
        bookService.deleteById(id);

        return REDIRECT_TO_MAIN_PAGE;
    }
}
