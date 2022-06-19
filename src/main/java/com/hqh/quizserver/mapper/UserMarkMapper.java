package com.hqh.quizserver.mapper;

import com.hqh.quizserver.entities.TestQuizz;
import com.hqh.quizserver.entities.User;
import com.hqh.quizserver.entities.UserMark;
import com.hqh.quizserver.dto.UserMarkDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMarkMapper {

    UserMarkMapper INSTANCE = Mappers.getMapper(UserMarkMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "mark", ignore = true)
    @Mapping(target = "pointLock", ignore = true)
    @Mapping(target = "testQuizz", source = "testQuizz")
    @Mapping(target = "user", source = "user")
    UserMark map(UserMarkDto userMarkDto, TestQuizz testQuizz, User user);

    @Mapping(target = "quizzId", expression = "java(userMark.getTestQuizz().getId())")
    @Mapping(target = "username", expression = "java(userMark.getUser().getUsername())")
    @Mapping(target = "userId", expression = "java(userMark.getUser().getId())")
    @Mapping(target = "quizzName", expression = "java(userMark.getTestQuizz().getTestName())")
    UserMarkDto mapToDto(UserMark userMark);

}
