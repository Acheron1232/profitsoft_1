package com.acheron.service;

import com.acheron.exceptions.SystemArgsException;
import com.acheron.parser.JsonParser;
import com.acheron.writer.XmlWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class MainServiceTest {

    static class DummyParser implements JsonParser<File, Map<String, Long>> {
        @Override
        public Map<String, Long> parse(File file, String attribute) {
            ConcurrentHashMap<String, Long> map = new ConcurrentHashMap<>();
            map.put("test", 1L);
            return map;
        }

        @Override
        public Map<String, Long> parse(File file, java.util.concurrent.ExecutorService executor, String attribute) {
            return parse(file, attribute);
        }
    }

    static class DummyWriter implements XmlWriter<Map<String, Long>> {
        boolean called = false;

        @Override
        public void convert(Map<String, Long> map, String attributeName) {
            called = true;
            assertNotNull(map);
            assertEquals(1L, map.get("test"));
            assertEquals("value", attributeName, "Just for demonstration, attribute name can be anything");
        }
    }

    private MainService<Map<String, Long>> service;
    private DummyWriter writer;

    @BeforeEach
    void setup() {
        service = new MainService<>(new DummyParser(), writer = new DummyWriter());
    }

    @Test
    void testProcessWithCorrectArgs() {
        String[] args = {"dummyPath", "value"};
        assertDoesNotThrow(() -> service.process(args));
        assertTrue(writer.called, "Writer should be called");
    }

    @Test
    void testProcessWithWrongArgs() {
        String[] args = {"onlyOneArg"};
        assertThrows(SystemArgsException.class, () -> service.process(args));
    }

    @Test
    void testProcessWithNullArgs() {
        assertThrows(SystemArgsException.class, () -> service.process(new String[]{}));
    }
}
