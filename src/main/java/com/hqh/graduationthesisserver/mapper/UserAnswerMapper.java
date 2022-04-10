package com.hqh.graduationthesisserver.mapper;

import com.hqh.graduationthesisserver.domain.Question;
import com.hqh.graduationthesisserver.domain.TestQuizz;
import com.hqh.graduationthesisserver.domain.User;
import com.hqh.graduationthesisserver.domain.UserAnswer;
import com.hqh.graduationthesisserver.dto.UserAnswerDto;
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
    UserAnswer map(UserAnswerDto userAnswerDto, TestQuizz testQuizz, Question question, User user);

    @Mapping(target = "quizzId", expression = "java(userAnswer.getTestQuizz().getId())")
    @Mapping(target = "questionId", expression = "java(userAnswer.getQuestion().getId())")
    @Mapping(target = "userId", expression = "java(userAnswer.getUser().getId())")
    UserAnswerDto mapToDto(UserAnswer userAnswer);

}
