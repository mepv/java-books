package com.accenture.booklibrary.controller;

import com.accenture.booklibrary.model.Book;
import com.accenture.booklibrary.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/title/{title}")
    @PreAuthorize("hasAuthority('book:read')")
    @PostAuthorize("@accessValidation.hasCategoryAuthority(returnObject.category, authentication) " +
            "&& @accessValidation.hasAuthorAuthority(returnObject.author, authentication)")
    public Book getBookByTitle(@PathVariable("title") String title) {
        return bookService.getBookByTitle(title);
    }

    @GetMapping("/author/{author}")
    @PreAuthorize("hasAuthority('book:read')")
    @PostFilter("@accessValidation.hasCategoryAuthority(filterObject.category, authentication) " +
            "&& @accessValidation.hasAuthorAuthority(filterObject.author, authentication)")
    public List<Book> getBooksByAuthor(@PathVariable("author") String author) {
        return bookService.getBooksByAuthor(author);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('book:write')")
    public ResponseEntity<Void> createBook(@RequestBody Book book) {
        String location = bookService.createBook(book);
        return ResponseEntity.created(URI.create(location.replace(" ", "%20"))).build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('book:write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable("id") Long id) {
        bookService.deleteBook(id);
    }

    @GetMapping("/categories")
    public Set<String> getCategories() {
        return bookService.getCategories();
    }

    @GetMapping("/authors")
    public Set<String> getAuthors() {
        return bookService.getAuthors();
    }

    @GetMapping("/titles")
    public List<String> getTitles() {
        return bookService.getTitles();
    }
}
