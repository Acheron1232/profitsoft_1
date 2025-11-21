package com.acheron.parser;

import java.util.concurrent.ExecutorService;

/**
 * This interface is used to parse P parameter from R supplier.
 *
 * @param <P>
 */
@FunctionalInterface // this interface can be used as a functional interface
public interface JsonParser<P, R> {

    R parse(P input, String attribute);

    default R parse(P file, ExecutorService executor, String attribute) {
        return parse(file, attribute); // default implementation
    }

}
