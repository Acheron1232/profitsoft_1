package com.acheron.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Author {

    private UUID id;

    private String firstName;

    private String lastName;

    private List<UUID> books;
}
