package com.hqh.quizserver.constant;

public class PatternConstant {

    public static final String EMAIL_PATTERN = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
    public static final String PASSWORD_PATTERN = "^(?=.*?[a-z])(?=.*?[0-9]).{8,}$";
    public static final String USERNAME_PATTERN = "^[A-Za-z][A-Za-z0-9_]{3,20}$";
    public static final String NAME_PATTERN = "\\b([A-ZÀ-ÿ][-,a-z. ']+[ ]*)+";

}
