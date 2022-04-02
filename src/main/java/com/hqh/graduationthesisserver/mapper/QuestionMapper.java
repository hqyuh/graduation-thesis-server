package com.hqh.graduationthesisserver.mapper;

import com.hqh.graduationthesisserver.domain.Question;
import com.hqh.graduationthesisserver.domain.TestQuizz;
import com.hqh.graduationthesisserver.dto.QuestionDto;
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
    Question map(QuestionDto questionDto, TestQuizz testQuizz);

    @Mapping(target = "quizzId", expression = "java(question.getTestQuizz().getId())")
    QuestionDto mapToDto(Question question);

}
