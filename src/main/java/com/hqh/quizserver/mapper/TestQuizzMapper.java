package com.hqh.quizserver.mapper;

import com.hqh.quizserver.domain.TestQuizz;
import com.hqh.quizserver.domain.Topic;
import com.hqh.quizserver.dto.TestQuizzDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TestQuizzMapper {

    TestQuizzMapper INSTANCE = Mappers.getMapper(TestQuizzMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "testName", ignore = true)
    @Mapping(target = "examTime", ignore = true)
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "isStart", ignore = true)
    @Mapping(target = "isEnd", ignore = true)
    @Mapping(target = "activationCode", ignore = true)
    @Mapping(target = "topic", source = "topic")
    TestQuizz map(TestQuizzDto testQuizzDto, Topic topic);

    @Mapping(target = "topicId", expression = "java(testQuizz.getTopic().getId())")
    TestQuizzDto mapToDto(TestQuizz testQuizz);

}
