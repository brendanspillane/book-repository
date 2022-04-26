package com.bookrepo.repo;

import com.bookrepo.model.Author;
import com.bookrepo.model.Book;
import com.bookrepo.model.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IBookRepository extends CrudRepository<Book, Long> {
    List<Book> findByCategories(Category category);
    List<Book> findByAuthor(Author author);
}
