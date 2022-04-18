package com.hqh.graduationthesisserver.mapper;

import com.hqh.graduationthesisserver.domain.TestQuizz;
import com.hqh.graduationthesisserver.domain.User;
import com.hqh.graduationthesisserver.domain.UserMark;
import com.hqh.graduationthesisserver.dto.UserMarkDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMarkMapper {

    UserMarkMapper INSTANCE = Mappers.getMapper(UserMarkMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "mark", ignore = true)
    @Mapping(target = "testQuizz", source = "testQuizz")
    @Mapping(target = "user", source = "user")
    UserMark map(UserMarkDto userMarkDto, TestQuizz testQuizz, User user);

    @Mapping(target = "quizzId", expression = "java(userMark.getTestQuizz().getId())")
    @Mapping(target = "username", expression = "java(userMark.getUser().getUsername())")
    @Mapping(target = "quizzName", expression = "java(userMark.getTestQuizz().getTestName())")
    UserMarkDto mapToDto(UserMark userMark);

}
