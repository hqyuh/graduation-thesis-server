package com.hqh.quizserver.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CommonUtils {

    public static boolean isNull(String value) {
        return value == null || value.length() == 0;
    }

    public static <T> List<List<T>> splitSection(List<T> objects, int number) {
        return new ArrayList<>(IntStream.range(0 , objects.size())
                .boxed()
                .collect(Collectors.groupingBy(e -> e / number, Collectors.mapping(objects::get, Collectors.toList())))
                .values()
        );
    }

}
