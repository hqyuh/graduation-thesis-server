package com.hqh.quizserver.mapper;

import com.hqh.quizserver.entities.Question;
import com.hqh.quizserver.entities.TestQuizz;
import com.hqh.quizserver.dto.QuestionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

    QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "topicQuestion", ignore = true)
    @Mapping(target = "questionImageUrl", ignore = true)
    @Mapping(target = "answerA", ignore = true)
    @Mapping(target = "answerB", ignore = true)
    @Mapping(target = "answerC", ignore = true)
    @Mapping(target = "answerD", ignore = true)
    @Mapping(target = "correctResult", ignore = true)
    @Mapping(target = "mark", ignore = true)
    @Mapping(target = "testQuizz", source = "testQuizz")
    Question map(QuestionDTO questionDto, TestQuizz testQuizz);

    @Mapping(target = "quizzId", expression = "java(question.getTestQuizz().getId())")
    QuestionDTO mapToDto(Question question);

}
