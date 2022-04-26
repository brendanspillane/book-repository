package com.bookrepo.repo;

import com.bookrepo.model.Category;
import org.springframework.data.repository.CrudRepository;

public interface ICategoryRepository extends CrudRepository<Category, Long> {

}
