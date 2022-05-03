package com.bookrepo.service;

import com.bookrepo.model.Author;
import com.bookrepo.model.Book;
import com.bookrepo.model.BookLoan;
import com.bookrepo.model.Member;

import java.time.LocalDateTime;
import java.util.List;

public interface IBookRepoService {

    BookLoan loanBook(String email, Long bookId, LocalDateTime loanedDate);

    BookLoan returnBook(Long bookLoanId, LocalDateTime returnDate);

    List<BookLoan> getBookLoans();

    BookLoan getBookLoan(Long id);

    boolean removeBook(Long id);

    Book addBook(Book book);

    Author addAuthor(Author author);

    Member addMember(Member member);
}
