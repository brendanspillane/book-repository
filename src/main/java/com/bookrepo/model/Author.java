package com.bookrepo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class Author {

    @Id
    @GeneratedValue
    private Long id;

    private String firstName;
    private String lastName;
}
