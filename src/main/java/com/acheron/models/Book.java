package com.acheron.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Book {

    private UUID id;

    private UUID authorId;

    private String title;

    /**
     * ISBN stands for International Standard Book Number,
     * a unique 10 or 13-digit identifier assigned to each specific edition of a book.
     */
    private String isbn;

    private List<String> genres;

    private Instant publishDate;
}
