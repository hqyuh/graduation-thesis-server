package com.hqh.quizserver.mapper;

import com.hqh.quizserver.dto.UserMarkDTO;
import com.hqh.quizserver.entity.TestQuizz;
import com.hqh.quizserver.entity.User;
import com.hqh.quizserver.entity.UserMark;
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
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    UserMark convertDTOToUserMark(UserMarkDTO userMarkDTO, TestQuizz testQuizz, User user);

    @Mapping(target = "username", expression = "java(userMark.getUser().getUsername())")
    @Mapping(target = "quizzName", expression = "java(userMark.getTestQuizz().getTestName())")
    UserMarkDTO convertUserMarkToDTO(UserMark userMark);

}
