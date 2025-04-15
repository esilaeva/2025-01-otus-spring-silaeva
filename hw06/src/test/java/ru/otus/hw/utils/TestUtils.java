package ru.otus.hw.utils;

import java.util.stream.LongStream;
import java.util.stream.Stream;

public class TestUtils {

    public static Stream<Long> generateIndexesSequence(long from, long to) {
        return LongStream.rangeClosed(from, to).boxed();
    }
}
