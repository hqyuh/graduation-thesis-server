package com.hqh.quizserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class QuizServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizServerApplication.class, args);
    }

}
