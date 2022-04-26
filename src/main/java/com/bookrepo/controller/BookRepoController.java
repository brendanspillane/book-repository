package com.bookrepo.controller;

import com.bookrepo.model.Author;
import com.bookrepo.model.Book;
import com.bookrepo.model.BookLoan;
import com.bookrepo.service.IBookRepoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO Validation:
 * e.g. when adding a book, title should be a required field.
 *
 * TODO Add additional rest endpoints for:
 * updating/deleting authors & categories.
 * finding/searching for books/members
 */
@RestController
@RequestMapping("/api/v1")
public class BookRepoController {

    @Autowired
    private IBookRepoService bookService;

    @PostMapping("/books")
    @ResponseStatus(code = HttpStatus.CREATED)
    public Book addBook(@RequestBody Book book) {

        return bookService.addBook(book);
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Long> removeBook(@PathVariable Long id) {

        if (!bookService.removeBook(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @PostMapping("/members/{email}/books/{bookId}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public BookLoan loanBook(@PathVariable String email, @PathVariable Long bookId) {
        return bookService.loanBook(email, bookId);
    }

    @PutMapping("/members/{email}/books/{bookId}")
    public BookLoan returnBook(@PathVariable String email, @PathVariable Long bookId) {
        return bookService.returnBook(email, bookId);
    }

    @PostMapping("/authors")
    public Author addAuthor(Author author) {
        return bookService.addAuthor(author);
    }
}
