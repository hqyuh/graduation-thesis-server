package com.hqh.quizserver.services;

import com.hqh.quizserver.dto.TopicDTO;
import com.hqh.quizserver.entity.Topic;
import com.hqh.quizserver.exceptions.domain.topic.TopicExistException;
import com.hqh.quizserver.exceptions.domain.topic.TopicNotFoundException;

import java.util.List;

public interface TopicService {

    Topic createTopic(String topicName) throws TopicNotFoundException, TopicExistException;

    Topic updateTopic(String currentTopicName, String newTopicName) throws TopicNotFoundException, TopicExistException;

    List<TopicDTO> getAllTopic();

    void deleteTopic(Long id);

    TopicDTO getTopicByID(Long id);

}
