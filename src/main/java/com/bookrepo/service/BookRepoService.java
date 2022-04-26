package com.bookrepo.service;

import com.bookrepo.exception.BookNotFoundException;
import com.bookrepo.exception.LoanNotFoundException;
import com.bookrepo.exception.MaxLoansException;
import com.bookrepo.exception.MemberNotFoundException;
import com.bookrepo.exception.OverdueLoanException;
import com.bookrepo.model.Author;
import com.bookrepo.model.Book;
import com.bookrepo.model.BookLoan;
import com.bookrepo.model.Member;
import com.bookrepo.repo.IAuthorRepository;
import com.bookrepo.repo.IBookLoanRepository;
import com.bookrepo.repo.IBookRepository;
import com.bookrepo.repo.ICategoryRepository;
import com.bookrepo.repo.IMemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * TODO: add additional methods for updating authors/categories etc.
 */
@Service
public class BookRepoService implements IBookRepoService {

    Logger logger = LoggerFactory.getLogger(BookRepoService.class);

    private static int MaxLoansPerMember = 3;
    private static int PermittedLoanPeriodInDays = 7;
    @Autowired
    IBookRepository bookRepository;

    @Autowired
    IAuthorRepository authorRepository;

    @Autowired
    IBookLoanRepository bookLoanRepository;

    @Autowired
    IMemberRepository memberRepository;

    @Autowired
    ICategoryRepository categoryRepository;

    /**
     * Maximum number of books loaned at any time is 3 per user.
     * If a member has any outstanding loaned books, they cannot loan any more until all books returned.
     *
     * @param memberId
     * @param bookId
     * @return
     */
    @Override
    public BookLoan loanBook(String memberId, Long bookId) {

        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            throw new MemberNotFoundException(String.format("Could not find member with ID %s.", memberId));
        }
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            throw new BookNotFoundException(String.format("Could not find book with ID %s.", bookId));
        }

        int loanedBooksCount = bookLoanRepository.countByMemberAndReturnedDateIsNull(member);
        if (loanedBooksCount >= MaxLoansPerMember) {
            throw new MaxLoansException(String.format("Cannot Loan this Book as this would exceed the maximum number of books that can be loaned at any one time. User has %s books loaned already. ", loanedBooksCount));
        }

        int overdueBooksCount = bookLoanRepository.countByMemberAndReturnedDateIsNullAndLoanedDateLessThan(
            member,
            java.util.Date.from(LocalDate.now().minusDays(PermittedLoanPeriodInDays).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
        );

        if (overdueBooksCount != 0) {
            throw new OverdueLoanException(String.format("Cannot Loan this Book, member has %s book(s) overdue. ", overdueBooksCount));
        }

        BookLoan bookLoan = new BookLoan();
        bookLoan.setBook(book);
        bookLoan.setMember(member);

        bookLoan.setLoanedDate(new Date());
        return bookLoanRepository.save(bookLoan);
    }

    /**
     *
     * @param memberId
     * @param bookId
     * @return
     */
    @Override
    public BookLoan returnBook(String memberId, Long bookId) {

        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            throw new MemberNotFoundException(String.format("Could not find member with ID %s.", memberId));
        }
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            throw new BookNotFoundException(String.format("Could not find book with ID %s.", bookId));
        }

        BookLoan bookLoan = bookLoanRepository.findByMemberAndBookAndReturnedDateIsNull(memberRepository.findById(memberId).orElse(null), bookRepository.findById(bookId).orElse(null));
        if (bookLoan == null) {
            throw new LoanNotFoundException(String.format("Could not find a record of this book id (%s) being loaned out to member id %s.", bookId, memberId));
        }
        bookLoan.setReturnedDate(new Date());
        return bookLoanRepository.save(bookLoan);
    }

    /**
     *
     * @param book
     * @return
     */
    @Override
    public Book addBook(Book book) {
        Author author = book.getAuthor();
        if (author.getId() == null || !authorRepository.existsById(author.getId())) {
            book.setAuthor(authorRepository.save(author));
        }

        return bookRepository.save(book);
    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    public boolean removeBook(Long id) {
        if (bookRepository.findById(id).isPresent()) {
            bookRepository.deleteById(id);
            return true;
        }

        return false;
    }

    /**
     *
     * @param author
     * @return
     */
    @Override
    public Author addAuthor(Author author) {
        return author;
    }

    /**
     *
     * @param member
     * @return
     */
    @Override
    public Member addMember(Member member) {
        return memberRepository.save(member);
    }
}
