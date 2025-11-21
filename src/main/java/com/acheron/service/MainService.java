package com.acheron.service;

import com.acheron.exceptions.SystemArgsException;
import com.acheron.writer.XmlWriter;
import com.acheron.parser.JsonParser;
import com.acheron.util.ColorPrinter;

import java.io.File;
import java.util.concurrent.ExecutorService;

public record MainService<T>(JsonParser<File, T> jsonParser, XmlWriter<T> xmlWriter) {

    /**
     * Default process method: uses parser-managed executor if needed.
     */
    public void process(String[] args) {
        processInternal(args, null);
    }

    /**
     * Process with an external ExecutorService.
     */
    public void process(String[] args, ExecutorService executor) {
        processInternal(args, executor);
    }

    private void processInternal(String[] args, ExecutorService executor) {
        // GraalVM native-image support
        boolean isNative = System.getProperty("org.graalvm.nativeimage.imagecode") != null;

        if (args.length != 2 && !isNative) {
            ColorPrinter.println(ColorPrinter.PURPLE, "Usage: java -jar ./app.jar <path_to_folder>/<path_to_file> <attribute>");
            ColorPrinter.println(ColorPrinter.PURPLE, "Or use -h/--help");
            throw new SystemArgsException("Incorrect arguments");
        } else if (args.length != 2) {
            ColorPrinter.println(ColorPrinter.PURPLE, "Usage: ./app <path_to_folder>/<path_to_file> <attribute>");
            ColorPrinter.println(ColorPrinter.PURPLE, "Or use -h/--help");
            throw new SystemArgsException("Incorrect arguments");
        }

        String path = args[0];
        String attribute = args[1];

        T parsedData;
        if (executor != null) {
            parsedData = jsonParser.parse(new File(path), executor, attribute);
        } else {
            parsedData = jsonParser.parse(new File(path), attribute);
        }

        xmlWriter.convert(parsedData, attribute);

        ColorPrinter.println(ColorPrinter.GREEN,"Converted items");
    }

}