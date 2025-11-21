package com.acheron.writer;

import com.acheron.exceptions.XmlWritingException;
import com.acheron.util.ColorPrinter;
import com.acheron.dto.XmlDataSchemaDto;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

/**
 * Simple XML writer for ConcurrentMap<String, Long>.
 * Writes a map to XML in "./src/main/resources/output/xml/statistics_by_<attribute>.xml"
 */
public class SingleThreadedWriter implements XmlWriter<ConcurrentMap<String, Long>> {

    private final XmlMapper xmlMapper = XmlMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .build();

    @Override
    public void convert(ConcurrentMap<String, Long> map, String attributeName) {
        File file = new File("./src/main/resources/output/xml/statistics_by_" + attributeName + ".xml");
        file.getParentFile().mkdirs();

        try {
            // Wrap map into a DTO for clean XML structure
            XmlDataSchemaDto wrapper = new XmlDataSchemaDto();
            map.entrySet().stream()
                    .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                    .forEach(entry -> wrapper.addItem(new XmlDataSchemaDto.ItemDto(entry.getKey(), entry.getValue())));


            xmlMapper.writeValue(file, wrapper);

            ColorPrinter.println(ColorPrinter.GREEN, "XML file written: " + file.getAbsolutePath());

        } catch (IOException ex) {
            throw new XmlWritingException("Failed to write XML for attribute: " + attributeName, ex);
        }
    }
}
