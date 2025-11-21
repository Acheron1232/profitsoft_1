    package com.acheron.parser;

    import com.fasterxml.jackson.databind.MappingIterator;
    import com.fasterxml.jackson.databind.ObjectMapper;

    import java.io.File;
    import java.io.IOException;
    import java.util.Arrays;
    import java.util.Spliterator;
    import java.util.Spliterators;
    import java.util.stream.Stream;
    import java.util.stream.StreamSupport;

    public class StreamingJsonParser<T> implements JsonParser<File, Stream<T>> {

        private final ObjectMapper mapper = new ObjectMapper();
        private final Class<T> type;

        public StreamingJsonParser(Class<T> type) {
            this.type = type;
        }

        @Override
        public Stream<T> parse(File dir, String attribute) {
            File[] fileArray = dir.listFiles();
            if (fileArray == null) {
                throw new RuntimeException();
            }

            return Arrays.stream(fileArray)
                    .flatMap(this::streamFile);
        }

        private Stream<T> streamFile(File file) {
            try {
                MappingIterator<T> iterator = mapper.readerFor(type).readValues(file);
                return StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                        false
                );
            } catch (IOException e) {
                throw new RuntimeException("Error parsing " + file, e);
            }
        }
    }
