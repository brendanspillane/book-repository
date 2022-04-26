package com.bookrepo.service;

import com.bookrepo.model.Author;
import com.bookrepo.model.Book;
import com.bookrepo.model.BookLoan;
import com.bookrepo.model.Member;

public interface IBookRepoService {

    BookLoan loanBook(String email, Long bookId);

    BookLoan returnBook(String email, Long bookId);

    boolean removeBook(Long id);

    Book addBook(Book book);

    Author addAuthor(Author author);

    Member addMember(Member member);
}
