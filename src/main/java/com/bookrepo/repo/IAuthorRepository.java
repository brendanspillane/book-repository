package com.bookrepo.repo;

import com.bookrepo.model.Author;
import com.bookrepo.model.Category;
import org.springframework.data.repository.CrudRepository;

public interface IAuthorRepository extends CrudRepository<Author, Long> {

}
