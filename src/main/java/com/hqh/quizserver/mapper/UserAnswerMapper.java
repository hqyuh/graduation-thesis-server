package com.hqh.quizserver.mapper;

import com.hqh.quizserver.dto.UserAnswerDTO;
import com.hqh.quizserver.dto.UserDTO;
import com.hqh.quizserver.entities.Question;
import com.hqh.quizserver.entities.TestQuizz;
import com.hqh.quizserver.entities.User;
import com.hqh.quizserver.entities.UserAnswer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface UserAnswerMapper {

    UserAnswerMapper INSTANCE = Mappers.getMapper(UserAnswerMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "testQuizz", source = "testQuizz")
    @Mapping(target = "question", source = "question")
    @Mapping(target = "user", source = "userDTO")
    @Mapping(target = "isSelected", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    UserAnswer map(UserAnswerDTO userAnswerDto, TestQuizz testQuizz, Question question, UserDTO userDTO);

    @Mapping(target = "quizzId", expression = "java(userAnswer.getTestQuizz().getId())")
    @Mapping(target = "questionId", expression = "java(userAnswer.getQuestion().getId())")
    @Mapping(target = "userId", expression = "java(userAnswer.getUser().getId())")
    UserAnswerDTO mapToDto(UserAnswer userAnswer);

}
