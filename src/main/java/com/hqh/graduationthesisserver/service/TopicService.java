package com.hqh.graduationthesisserver.service;

import com.hqh.graduationthesisserver.domain.Topic;
import com.hqh.graduationthesisserver.exception.domain.topic.TopicExistException;
import com.hqh.graduationthesisserver.exception.domain.topic.TopicNotFoundException;

import java.util.List;

public interface TopicService {

    Topic createTopic(String topicName)
            throws TopicNotFoundException, TopicExistException;

    Topic updateTopic(String currentTopicName, String newTopicName)
            throws TopicNotFoundException, TopicExistException;

    List<Topic> getAllTopic();

    void deleteTopic(Long id);

}
