package de.itemis.graphing.util;

import java.util.function.Function;
import java.util.stream.Stream;

public class StreamUtil
{

    public static <T, R> Function<T, Stream<R>> ofType(Class<R> clazz)
    {
        return e -> clazz.isInstance(e) ? Stream.of(clazz.cast(e)) : null;
    }
}
