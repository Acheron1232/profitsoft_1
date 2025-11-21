package com.acheron.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "statistics")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class XmlDataSchemaDto {

    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ItemDto> item = new ArrayList<>();

    public void addItem(ItemDto itemDto) {
        item.add(itemDto);
    }
    @JacksonXmlRootElement(localName = "item")
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class ItemDto {

        @JacksonXmlProperty(localName = "value")
        private String value;

        @JacksonXmlProperty(localName = "count")
        private long count;
    }
}
