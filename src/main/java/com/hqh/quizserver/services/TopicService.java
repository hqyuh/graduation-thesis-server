package com.hqh.quizserver.services;

import com.hqh.quizserver.entities.Topic;
import com.hqh.quizserver.exceptions.domain.topic.TopicExistException;
import com.hqh.quizserver.exceptions.domain.topic.TopicNotFoundException;

import java.util.List;

public interface TopicService {

    Topic createTopic(String topicName)
            throws TopicNotFoundException, TopicExistException;

    Topic updateTopic(String currentTopicName, String newTopicName)
            throws TopicNotFoundException, TopicExistException;

    List<Topic> getAllTopic();

    void deleteTopic(Long id);

}
