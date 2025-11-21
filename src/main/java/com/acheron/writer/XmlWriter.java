package com.acheron.writer;


import java.util.Map;

/**
 * This interface is used to convert P parameter to XML format.
 *
 * @param <P>
 */
@FunctionalInterface // this interface can be used as a functional interface
public interface XmlWriter<P> {

    void convert(P list,  String attribute);

}
