package com.acheron.util.excludes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class JsonGenerator {

    public static void generateAuthorsJson(int numberOfAuthors, int maxBooksPerAuthor, int files) throws IOException {
        for (int z = 0; z < files; z++) {
            File file = new File("./src/main/resources/authors/author_" + UUID.randomUUID() + ".json");
            file.getParentFile().mkdirs();
            List<AuthorDto> authors = new ArrayList<>();
            for (int i = 1; i <= numberOfAuthors; i++) {
                String firstName = "FirstName" + i;
                String lastName = "LastName" + i;
                UUID authorId = UUID.randomUUID();

                int booksCount = (int) (Math.random() * maxBooksPerAuthor) + 1;
                List<String> bookIds = new ArrayList<>();
                for (int j = 0; j < booksCount; j++) {
                    bookIds.add(UUID.randomUUID().toString());
                }

                authors.add(new AuthorDto(authorId.toString(), firstName, lastName, bookIds));
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(file, authors);
        }
    }

    public static class AuthorDto {
        public String id;
        public String firstName;
        public String lastName;
        public List<String> books;

        public AuthorDto(String id, String firstName, String lastName, List<String> books) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.books = books;
        }
    }

    public static void main(String[] args) throws IOException {
        generateAuthorsJson(10000, 13, 100);
    }
}