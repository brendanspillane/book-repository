package com.bookrepo.controller;

import com.bookrepo.Application;
import com.bookrepo.controller.dto.LoanRequest;
import com.bookrepo.controller.dto.ReturnRequest;
import com.bookrepo.exception.BookNotFoundException;
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
import com.bookrepo.repo.IMemberRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@AutoConfigureMockMvc
@EnableAutoConfiguration()
@AutoConfigureTestDatabase
public class BookRepoControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    IBookRepository bookRepository;

    @Autowired
    IAuthorRepository authorRepository;

    @Autowired
    IBookLoanRepository bookLoanRepository;

    @Autowired
    IMemberRepository memberRepository;

    @After
    public void resetDb() {
        bookLoanRepository.deleteAll();
        memberRepository.deleteAll();
        bookRepository.deleteAll();
        authorRepository.deleteAll();
    }

    @Test
    public void givenBook_whenLoaned_thenCreateBookLoan() throws Exception {
        Author author = createAuthor("Mario",  "Puzo");
        createBook(Long.valueOf(12345),  "The godfather",  author);
        createBook(Long.valueOf(2),  "The godfather Pt II",  author);
        Member member = createMember("test@test.com");

        LoanRequest request = new LoanRequest(Long.valueOf(12345), member.getEmail(), LocalDateTime.now());

        mvc.perform(post("/api/v1/bookloans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString()) )
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        List<BookLoan> bookLoan = bookLoanRepository.findByMember(member);
        assertThat(bookLoan).hasSize(1);
        assertThat(bookLoan.get(0)).extracting(BookLoan::getLoanedDate).isNotNull();
        assertThat(bookLoan.get(0)).extracting(BookLoan::getReturnedDate).isNull();
    }

    @Test
    public void givenBookLoan_whenReturned_thenUpdateBookLoan() throws Exception {
        Author author = createAuthor("Mario",  "Puzo");
        Book book = createBook(Long.valueOf(12345),  "The godfather",  author);
        Member member = createMember("test@test.com");

        BookLoan initialBookLoan = createBookLoan(book, member);

        ReturnRequest request = new ReturnRequest(initialBookLoan.getId(), LocalDateTime.now());

        mvc.perform(patch("/api/v1/bookloans/" + initialBookLoan.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString()) )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        List<BookLoan> updatedBookLoan = bookLoanRepository.findByMember(member);
        assertThat(updatedBookLoan).hasSize(1);
        assertThat(updatedBookLoan.get(0)).extracting(BookLoan::getLoanedDate).isNotNull();
        assertThat(updatedBookLoan.get(0)).extracting(BookLoan::getReturnedDate).isNotNull();
        assertThat(updatedBookLoan.get(0)).extracting(BookLoan::getId).isEqualTo(initialBookLoan.getId());
    }

    @Test
    public void given3BooksLoaned_whenLoan_thenException() throws Exception {
        Author author = createAuthor("Mario",  "Puzo");
        Member member = createMember("test@test.com");
        Book book1 = createBook(Long.valueOf(12345),  "The godfather",  author);
        Book book2 = createBook(Long.valueOf(2),  "The godfather Pt II",  author);
        Book book3 = createBook(Long.valueOf(3),  "The godfather Pt III",  author);

        createBook(Long.valueOf(4),  "The godfather Pt IV",  author);

        createBookLoan(book1, member);
        createBookLoan(book2, member);
        createBookLoan(book3, member);

        LoanRequest request = new LoanRequest(Long.valueOf(4), member.getEmail(), LocalDateTime.now());

        mvc.perform(post("/api/v1/bookloans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString()) )
            .andDo(print())
            .andExpect(status().isInternalServerError())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof MaxLoansException))
            .andExpect(result -> assertEquals("Cannot Loan this Book as this would exceed the maximum number of books that can be loaned at any one time. User has 3 books loaned already.", result.getResolvedException().getMessage().trim()));

        List<BookLoan> updatedBookLoan = bookLoanRepository.findByMember(member);
        assertThat(updatedBookLoan).hasSize(3);
    }

    @Test
    public void givenBookLoanedOverdue_whenLoan_thenException() throws Exception {
        Author author = createAuthor("Mario",  "Puzo");
        Member member = createMember("test@test.com");
        Book book1 = createBook(Long.valueOf(12345),  "The godfather",  author);
        createBook(Long.valueOf(2),  "The godfather Pt II",  author);

        createBookLoan(book1, member, LocalDate.now().minusDays(10).atStartOfDay());

        LoanRequest request = new LoanRequest(Long.valueOf(2), member.getEmail(), LocalDateTime.now());

        mvc.perform(post("/api/v1/bookloans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString()) )
            .andDo(print())
            .andExpect(status().isInternalServerError())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof OverdueLoanException))
            .andExpect(result -> assertEquals("Cannot Loan this Book, member has 1 book(s) overdue.", result.getResolvedException().getMessage().trim()));

        assertThat(bookLoanRepository.findByMember(member)).hasSize(1);
    }

    @Test
    public void givenInvalidBookId_whenLoaned_thenException() throws Exception {
        Author author = createAuthor("Mario",  "Puzo");
        createBook(Long.valueOf(12345),  "The godfather",  author);
        Member member = createMember("test@test.com");

        LoanRequest request = new LoanRequest(Long.valueOf(1111), member.getEmail(), LocalDateTime.now());

        mvc.perform(post("/api/v1/bookloans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString()) )
            .andDo(print())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof BookNotFoundException))
            .andExpect(result -> assertEquals("Could not find book with ID 1111.", result.getResolvedException().getMessage().trim()));

        assertThat(bookLoanRepository.findByMember(member)).hasSize(0);
    }

    @Test
    public void givenInvalidMemberId_whenLoaned_thenException() throws Exception {
        Author author = createAuthor("Mario",  "Puzo");
        createBook(Long.valueOf(12345),  "The godfather",  author);
        Member member = createMember("test@test.com");

        LoanRequest request = new LoanRequest(Long.valueOf(12345), "test@invalid.com", LocalDateTime.now());

        mvc.perform(post("/api/v1/bookloans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString()) )
            .andDo(print())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof MemberNotFoundException))
            .andExpect(result -> assertEquals("Could not find member with ID test@invalid.com.", result.getResolvedException().getMessage().trim()));
    }

    @Test
    public void given1BookOnLoan_whenGetAllLoans_thenReturn1() throws Exception {
        Author author = createAuthor("Mario",  "Puzo");
        Member member = createMember("test@test.com");
        Book book1 = createBook(Long.valueOf(12345),  "The godfather",  author);
        Book book2 = createBook(Long.valueOf(2),  "The godfather Pt II",  author);

        BookLoan bookLoan = createBookLoan(book1, member, LocalDate.now().minusDays(10).atStartOfDay());
        createBookLoan(book2, member, LocalDate.now().minusDays(10).atStartOfDay(), LocalDate.now().minusDays(5).atStartOfDay()); // it has been returned so it won't be returned

        mvc.perform(get("/api/v1/bookloans")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*", hasSize(1)))
            .andExpect(jsonPath("$[0].id").value(bookLoan.getId()));

        assertThat(bookLoanRepository.findByMember(member)).hasSize(2);
    }

    private Book createBook(Long bookId, String bookTitle, Author author) {

        Book book = new Book();
        book.setId(bookId);
        book.setTitle(bookTitle);
        book.setAuthor(author);

        return bookRepository.save(book);
    }

    private BookLoan createBookLoan(Book book, Member member) {
        return createBookLoan(book, member, null);
    }

    private BookLoan createBookLoan(Book book, Member member, LocalDateTime loanDate, LocalDateTime returnedDate) {
        BookLoan bookLoan = new BookLoan();
        bookLoan.setBook(book);
        bookLoan.setMember(member);
        bookLoan.setLoanedDate(loanDate == null ? LocalDateTime.now() : loanDate);
        bookLoan.setReturnedDate(returnedDate);

        return bookLoanRepository.save(bookLoan);
    }

    private BookLoan createBookLoan(Book book, Member member, LocalDateTime loanDate) {
        return createBookLoan(book, member, loanDate, null);
    }

    private Author createAuthor(String authorFirstName, String authorLastName) {
        Author author = new Author();
        author.setFirstName(authorFirstName);
        author.setLastName(authorLastName);
        return authorRepository.save(author);
    }

    private Member createMember(String email) {
        Member member = new Member();
        member.setEmail(email);
        return memberRepository.save(member);
    }
}
