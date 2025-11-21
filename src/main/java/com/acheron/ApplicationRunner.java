package com.acheron;

import com.acheron.exceptions.SystemArgsException;
import com.acheron.models.Author;
import com.acheron.parser.ThreadedParser;
import com.acheron.service.MainService;
import com.acheron.util.ColorPrinter;
import com.acheron.writer.SingleThreadedWriter;

import java.util.concurrent.ConcurrentMap;

public class ApplicationRunner {

    public static void main(String[] args) {
        MainService<ConcurrentMap<String, Long>> authorMainService =
                new MainService<>(
                        new ThreadedParser(Author.class),
                        new SingleThreadedWriter());

        if (args.length == 0) {
            args = new String[] {"./src/main/resources/authors", "lastName"};
        }

        try{
            authorMainService.process(args); // You can set the executor .process(args, executorService);
        }catch (SystemArgsException e){
            ColorPrinter.println(ColorPrinter.RED, e.getMessage());
        }
    }
}