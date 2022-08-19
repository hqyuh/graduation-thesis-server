package com.hqh.quizserver.mapper;

import com.hqh.quizserver.dto.UserReportDTO;
import com.hqh.quizserver.entities.User;
import com.hqh.quizserver.entities.UserReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserReportMapper {
    UserReportMapper INSTANCE = Mappers.getMapper(UserReportMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "comment", ignore = true)
    @Mapping(target = "user", source = "user")
    UserReport map(UserReportDTO userReportDto, User user);

    @Mapping(target = "userId", expression = "java(userReport.getUser().getId())")
    UserReportDTO mapToDto(UserReport userReport);
}
