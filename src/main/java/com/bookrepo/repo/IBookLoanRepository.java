package com.bookrepo.repo;

import com.bookrepo.model.Book;
import com.bookrepo.model.BookLoan;
import com.bookrepo.model.Member;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface IBookLoanRepository extends CrudRepository<BookLoan, Long> {
    List<BookLoan> findByBook(Book book);
    List<BookLoan> findByMember(Member member);
    BookLoan findByMemberAndBookAndReturnedDateIsNull(Member member, Book book);
    int countByMemberAndReturnedDateIsNull(Member member);
    int countByMemberAndReturnedDateIsNullAndLoanedDateLessThan(Member member, Date date);
}
