package com.hqh.quizserver.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum LevelEnum {
    EASY("EASY", 1),
    MEDIUM( "MEDIUM", 2),
    HARD( "HARD", 3);

    private final String value;
    private final Integer numericValue;

    public static LevelEnum getLevel(String value) {
        return Arrays.stream(LevelEnum.values()).filter(e -> e.getValue().equals(value)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Not found Enum: " + value));
    }
}
