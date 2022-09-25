package com.hqh.quizserver.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum LevelQuestionEnum {

    BEGINNER("BEGINNER", 1),
    INTERMEDIATE("INTERMEDIATE", 2),
    LOW_ADVANCED("LOW_ADVANCED", 3),
    ADVANCED("ADVANCED", 4);

    private final String value;
    private final Integer numericValue;

    public static LevelQuestionEnum getLevel(String value) {
        return Arrays.stream(LevelQuestionEnum.values()).filter(e -> e.getValue().equals(value)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Not found Enum: " + value));
    }

}
