package com.bookrepo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.Date;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class BookLoan {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private Book book;

    @OneToOne
    private Member member;

    private Date loanedDate;

    private Date returnedDate;
}
