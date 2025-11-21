package com.acheron.parser;

import com.acheron.exceptions.InvalidInputException;
import com.acheron.exceptions.MissingAttributeException;
import com.acheron.TestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

class ThreadedParserTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private ThreadedParser parser;

    @BeforeEach
    void setup() {
        parser = new ThreadedParser(TestDto.class);
    }

    private File writeJson(Path dir, String fileName, TestDto dto) throws IOException {
        Path file = dir.resolve(fileName);
        mapper.writeValue(file.toFile(), dto);
        return file.toFile();
    }

    @Test
    void testParseSingleFile() throws IOException {
        Path temp = Files.createTempDirectory("json-test");
        writeJson(temp, "a.json", new TestDto("a,b"));

        var stats = parser.parse(temp.toFile(), "tags");

        assertEquals(2, stats.size());
        assertEquals(1L, stats.get("a"));
        assertEquals(1L, stats.get("b"));
    }

    @Test
    void testRecursiveParsing() throws IOException {
        Path root = Files.createTempDirectory("json-root");
        Path nested = Files.createDirectories(root.resolve("nested"));

        writeJson(root, "1.json", new TestDto("x"));
        writeJson(nested, "2.json", new TestDto("x,y"));

        var stats = parser.parse(root.toFile(), "tags");

        assertEquals(2, stats.size());
        assertEquals(2L, stats.get("x"));
        assertEquals(1L, stats.get("y"));
    }

    @Test
    void testMissingAttribute() throws IOException {
        Path temp = Files.createTempDirectory("json-test");
        writeJson(temp, "a.json", new TestDto("hello"));

        assertThrows(MissingAttributeException.class,
                () -> parser.parse(temp.toFile(), "wrongField"));
    }

    @Test
    void testInvalidInput() {
        assertThrows(InvalidInputException.class,
                () -> parser.parse(null, "tags"));
    }

    @Test
    void testEmptyDirectory() throws IOException {
        Path emptyDir = Files.createTempDirectory("empty-test");

        var stats = parser.parse(emptyDir.toFile(), "tags");

        assertTrue(stats.isEmpty());
    }
}
