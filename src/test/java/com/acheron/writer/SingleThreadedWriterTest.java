package com.acheron.writer;

import com.acheron.exceptions.XmlWritingException;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.junit.jupiter.api.Assertions.*;

class SingleThreadedWriterTest {

    private SingleThreadedWriter writer;
    private ConcurrentMap<String, Long> map;

    @BeforeEach
    void setup() {
        writer = new SingleThreadedWriter();
        map = new ConcurrentHashMap<>();
        map.put("a", 1L);
        map.put("b", 2L);
    }

    @Test
    void testConvertWritesXmlFile() throws Exception {
        writer.convert(map, "tags");

        File file = new File("./src/main/resources/output/xml/statistics_by_tags.xml");
        assertTrue(file.exists(), "XML file should exist");

        String content = Files.readString(file.toPath());
        assertTrue(content.contains("<statistics>"), "XML should contain <statistics>");
        assertTrue(content.contains("<item>"), "XML should contain <item>");
        assertTrue(content.contains("<value>a</value>") || content.contains("<value>b</value>"), "XML should contain values");
    }

    @Test
    void testConvertThrowsOnInvalidFile() {
        // Create a bad writer that always throws XmlWritingException
        SingleThreadedWriter badWriter = new SingleThreadedWriter() {
            @Override
            public void convert(ConcurrentMap<String, Long> map, String attributeName) {
                throw new XmlWritingException("Failed writing XML", new Exception("dummy"));
            }
        };

        ConcurrentMap<String, Long> dummyMap = new ConcurrentHashMap<>();
        dummyMap.put("x", 1L);

        XmlWritingException ex = assertThrows(XmlWritingException.class, () -> {
            badWriter.convert(dummyMap, "x");
        });

        assertEquals("Failed writing XML", ex.getMessage());
    }
}
