package com.bookrepo.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
public class LoanRequest {
    private Long bookId;
    private String memberId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loanedDate;

    @Override
    public String toString() {
        return "{" +
            "\"bookId\":" + bookId +
            ", \"memberId\":" + "\"" + memberId + "\"" +
            ", \"loanedDate\":" + "\"" + loanedDate + "\"" +
            '}';
    }
}
