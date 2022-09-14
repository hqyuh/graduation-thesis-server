package com.hqh.quizserver.mapper;

import com.hqh.quizserver.dto.UserAnswerQuestionRequestDTO;
import com.hqh.quizserver.entity.Question;
import com.hqh.quizserver.entity.TestQuizz;
import com.hqh.quizserver.entity.User;
import com.hqh.quizserver.entity.UserAnswer;
import com.hqh.quizserver.dto.UserAnswerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface UserAnswerMapper {

    UserAnswerMapper INSTANCE = Mappers.getMapper(UserAnswerMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "testQuizz", source = "testQuizz")
    @Mapping(target = "question", source = "question")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "isSelected", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    UserAnswer convertUserAnswerDTOToUserAnswerEntity(UserAnswerQuestionRequestDTO userAnswerQuestionRequestDTO, TestQuizz testQuizz, Question question, User user);

    @Mapping(target = "quizzId", expression = "java(userAnswer.getTestQuizz().getId())")
    @Mapping(target = "questionId", expression = "java(userAnswer.getQuestion().getId())")
    @Mapping(target = "userId", expression = "java(userAnswer.getUser().getId())")
    UserAnswerDTO mapToDto(UserAnswer userAnswer);

}
