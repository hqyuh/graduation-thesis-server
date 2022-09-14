package com.hqh.quizserver.dto;

public interface IReviewAnswerResponse  {
    String getTopicQuestion();
    String getAnswerA();
    String getAnswerB();
    String getAnswerC();
    String getAnswerD();

    String getIsSelected();
    String getCorrectResult();

    String getShortAnswer();
    String getCorrectEssay();

    boolean getIsCorrect();
}
