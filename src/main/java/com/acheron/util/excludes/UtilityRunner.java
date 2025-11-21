package com.acheron.util.excludes;

import com.acheron.models.Author;
import com.acheron.exceptions.SystemArgsException;
import com.acheron.parser.ThreadedParser;
import com.acheron.service.MainService;
import com.acheron.util.ColorPrinter;
import com.acheron.writer.SingleThreadedWriter;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Measures performance (time + memory) of the parsing pipeline.
 */
public class UtilityRunner {

    private static final String COLOR_MEMORY = ColorPrinter.CYAN;
    private static final String COLOR_TIME = ColorPrinter.GREEN;
    private static final String COLOR_AVG = ColorPrinter.YELLOW;
    private static final String COLOR_HEADER = ColorPrinter.PURPLE;

    public static void main(String[] args) {

        if (args.length == 0) {
            args = new String[] {"./src/main/resources/authors", "lastName"};
        }

        MainService<ConcurrentMap<String, Long>> authorMainService =
                new MainService<>(
                        new ThreadedParser(Author.class),
                        new SingleThreadedWriter());

        int totalMemoryMb = 0;
        long totalTimeMs = 0;

        int numberOfIterations = 3;

        ColorPrinter.println(COLOR_HEADER, "=== Performance Test (" + numberOfIterations + " runs) ===");
        System.out.println();

        for (int i = 0; i < numberOfIterations; i++) {

            long startTime = System.currentTimeMillis();
            try{

                authorMainService.process(args, Executors.newVirtualThreadPerTaskExecutor());
            }catch (SystemArgsException e){
                ColorPrinter.println(ColorPrinter.RED, e.getMessage());
            }
            long endTime = System.currentTimeMillis();

            long timeMs = endTime - startTime;
            int memoryMb = getUsedMemoryMb();

            totalTimeMs += timeMs;
            totalMemoryMb += memoryMb;

            ColorPrinter.println(COLOR_TIME,
                    "Run #" + (i + 1) + " Time: " + timeMs + " ms");

            ColorPrinter.println(COLOR_MEMORY,
                    "Run #" + (i + 1) + " Memory: " + memoryMb + " MB");

            System.out.println();
        }

        System.out.println();
        ColorPrinter.println(COLOR_AVG,
                "Average Memory: " + (totalMemoryMb / numberOfIterations) + " MB");
        ColorPrinter.println(COLOR_AVG,
                "Average Time: " + (totalTimeMs / numberOfIterations) + " ms");
        ColorPrinter.println(COLOR_AVG,
                "Total Time: " + totalTimeMs + " ms");
    }

    private static int getUsedMemoryMb() {
        Runtime runtime = Runtime.getRuntime();
        long used = runtime.totalMemory() - runtime.freeMemory();
        return (int) (used / 1024 / 1024);
    }
}
