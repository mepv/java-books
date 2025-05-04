package com.accenture.booklibrary.service;

import com.accenture.booklibrary.model.Book;
import com.accenture.booklibrary.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public Book getBookByTitle(String title) {
        return bookRepository.findByTitle(title)
                .orElseThrow(() -> new NoSuchElementException("There is not book with title: %s".formatted(title)));
    }

    @Transactional(readOnly = true)
    public List<Book> getBooksByAuthor(String author) {
        return bookRepository.findByAuthor(author);
    }

    @Transactional
    public String createBook(Book book) {
        Book b = bookRepository.save(book);
        return "/api/v1/books/" + b.getTitle();
    }

    @Transactional
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Set<String> getCategories() {
        return bookRepository.findAll()
                .stream()
                .map(Book::getCategory)
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public Set<String> getAuthors() {
        return bookRepository.findAll()
                .stream()
                .map(Book::getAuthor)
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public List<String> getTitles() {
        return bookRepository.findAll()
                .stream()
                .map(Book::getTitle)
                .toList();
    }
}
