package com.hqh.quizserver.mapper;

import com.hqh.quizserver.dto.UserDTO;
import com.hqh.quizserver.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", expression = "java(user.getId())")
    @Mapping(target = "firstName", expression = "java(user.getFirstName())")
    @Mapping(target = "lastName", expression = "java(user.getLastName())")
    @Mapping(target = "username", expression = "java(user.getUsername())")
    @Mapping(target = "email", expression = "java(user.getEmail())")
    @Mapping(target = "phoneNumber", expression = "java(user.getPhoneNumber())")
    @Mapping(target = "dateOfBirth", expression = "java(user.getDateOfBirth())")
    UserDTO userMapToUserDTO(User user);

}
