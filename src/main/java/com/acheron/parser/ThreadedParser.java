package com.acheron.parser;

import com.acheron.exceptions.InvalidInputException;
import com.acheron.exceptions.MissingAttributeException;
import com.acheron.exceptions.ParsingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadedParser implements JsonParser<File, ConcurrentMap<String, Long>> {

    private final ObjectMapper mapper = new ObjectMapper();
    private final Class<?> type;

    public ThreadedParser(Class<?> type) {
        this.type = type;
    }

    @Override
    public ConcurrentMap<String, Long> parse(File file, String attribute) {
        // Using virtual thread executor by default
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            return parse(file, executor, attribute);
        }
    }

    /**
     * Parses JSON files in a directory (or a single file) using the provided ExecutorService.
     * Each JSON file is processed in its own thread.
     *
     * @param file       single file or folder with JSONs
     * @param executor   thread executor
     * @param attribute  field name inside parsed object
     * @return statistics map
     *
     * @throws InvalidInputException      when file is null or unusable
     * @throws MissingAttributeException  when the attribute doesn't exist in the class
     * @throws ParsingException           when JSON or reflection fails
     */
    public ConcurrentMap<String, Long> parse(File file,
                                             ExecutorService executor,
                                             String attribute) {

        if (file == null) {
            throw new InvalidInputException("File is null");
        }

        List<File> jsonFiles = new ArrayList<>();
        collectJsonFiles(file, jsonFiles);

        ConcurrentHashMap<String, Long> stats = new ConcurrentHashMap<>();

        // Using reflection for dynamic objects
        final Field field;
        try {
            field = type.getDeclaredField(attribute);
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new MissingAttributeException("No such attribute: " + attribute, e);
        }

        // Count json files for CountDownLatch that provide full execution of threads
        int jsonCount = (int)jsonFiles.stream()
                .filter(f -> f.isFile() && f.getName().endsWith(".json"))
                .count();

        CountDownLatch latch = new CountDownLatch(jsonCount);
        ConcurrentLinkedQueue<RuntimeException> exceptions = new ConcurrentLinkedQueue<>();

        for (File f : jsonFiles) {
            if (!f.isFile() || !f.getName().endsWith(".json")) continue;

            executor.execute(() -> {
                try (MappingIterator<?> it = mapper.readerFor(type).readValues(f)) {
                    while (it.hasNext()) {
                        Object obj = it.next();
                        Object val = field.get(obj);

                        String key = val != null ? val.toString() : "";
                        String[] parts = key.split("\\s*,\\s*");

                        for (String part : parts) {
                            stats.merge(part, 1L, Long::sum);
                        }
                    }
                } catch (IOException | IllegalAccessException e) {
                    exceptions.add(new ParsingException("Failed to parse file: " + f.getName(), e));
                    throw new ParsingException("Failed to parse file: " + f.getName(), e);
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ParsingException("Parsing interrupted", e);
        }
        // Throwing any errors that occur in the threads
        if (!exceptions.isEmpty()) {
            throw exceptions.peek();
        }
        return stats;
    }

    private void collectJsonFiles(File file, List<File> result) {
        if (file.isFile() && file.getName().endsWith(".json")) {
            result.add(file);
            return;
        }

        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    collectJsonFiles(child, result);
                }
            }
        }
    }

}
