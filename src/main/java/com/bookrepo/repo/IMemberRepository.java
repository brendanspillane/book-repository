package com.bookrepo.repo;

import com.bookrepo.model.Member;
import org.springframework.data.repository.CrudRepository;

public interface IMemberRepository extends CrudRepository<Member, String> {
}
