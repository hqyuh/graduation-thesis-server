package com.hqh.quizserver.mapper;

import com.hqh.quizserver.dto.TopicDTO;
import com.hqh.quizserver.entity.Topic;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TopicMapper {

    TopicMapper INSTANCE = Mappers.getMapper(TopicMapper.class);

    TopicDTO mapToDTO(Topic topic);

    List<TopicDTO> convertTopicToDTO(List<Topic> topicList);

}
