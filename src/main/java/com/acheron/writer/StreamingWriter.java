package com.acheron.writer;

import com.acheron.dto.XmlDataSchemaDto;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class StreamingWriter<T> implements XmlWriter<Stream<T>> {

    private final XmlMapper xmlMapper = XmlMapper.builder().enable(SerializationFeature.INDENT_OUTPUT).build();
    private final Class<T> type;

    public StreamingWriter(Class<T> type) {
        this.type = type;
    }
    @Override
    public void convert(Stream<T> list, String attributeName) {
        File file = new File("./src/main/resources/output/xml/statistics_by_" + attributeName + ".xml");
        file.getParentFile().mkdirs();

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            fileWriter.write("<statistics>\n");

            Map<String, Integer> counts = new HashMap<>();

            list.forEach(e -> {
                try {
                    Field field = type.getDeclaredField(attributeName);
                    field.setAccessible(true);
                    Object valueObj = field.get(e);
                    String value = valueObj != null ? valueObj.toString() : "";

                    counts.put(value, counts.getOrDefault(value, 0) + 1);

                } catch (NoSuchFieldException | IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            });

            counts.forEach((value, count) -> {
                XmlDataSchemaDto.ItemDto item = new XmlDataSchemaDto.ItemDto(value, count);
                try {
                    String xml = xmlMapper.writeValueAsString(item);
                    fileWriter.write(xml + "\n");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

            fileWriter.write("</statistics>\n");

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
