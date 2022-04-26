package com.bookrepo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class Book {
    @Id
    private Long id; //isbn

    private String title;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "tagged",
        joinColumns = {
            @JoinColumn(name = "book_id", referencedColumnName = "id",
                nullable = false, updatable = false)},
        inverseJoinColumns = {
            @JoinColumn(name = "category_id", referencedColumnName = "id",
                nullable = false, updatable = false)})
    private List<Category> categories;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="author_id")
    private Author author; // list of authors?

}
