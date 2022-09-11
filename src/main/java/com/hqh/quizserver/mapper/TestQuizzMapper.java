package com.hqh.quizserver.mapper;

import com.hqh.quizserver.dto.TestQuizzResponseDTO;
import com.hqh.quizserver.entities.TestQuizz;
import com.hqh.quizserver.entities.Topic;
import com.hqh.quizserver.dto.TestQuizzDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;


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
    TestQuizz map(TestQuizzDTO testQuizzDTO, Topic topic);

    List<TestQuizzResponseDTO> listQuizzToQuizzResponseDTO(List<TestQuizz> testQuizz);

    TestQuizzResponseDTO convertQuizzToQuizzResponseDTO(TestQuizz testQuizz);

}
