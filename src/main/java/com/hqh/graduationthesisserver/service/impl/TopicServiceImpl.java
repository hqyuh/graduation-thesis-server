package com.hqh.graduationthesisserver.service.impl;

import com.hqh.graduationthesisserver.domain.Topic;
import com.hqh.graduationthesisserver.exception.domain.topic.TopicExistException;
import com.hqh.graduationthesisserver.exception.domain.topic.TopicNotFoundException;
import com.hqh.graduationthesisserver.repository.TopicRepository;
import com.hqh.graduationthesisserver.service.TopicService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.hqh.graduationthesisserver.constant.TopicConstant.NO_TOPIC_FOUND_BY_NAME;
import static com.hqh.graduationthesisserver.constant.TopicConstant.TOPIC_ALREADY_EXISTS;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;

    @Autowired
    public TopicServiceImpl(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    /***
     *
     * @param currentTopic
     * @param newTopic
     * @return
     * @throws TopicNotFoundException
     * @throws TopicExistException
     */
    private Topic validateNewTopicExists(String currentTopic,
                                         String newTopic)
            throws TopicNotFoundException, TopicExistException {
        Topic topic = topicRepository.findTopicByTopicName(newTopic);

        if(StringUtils.isNotBlank(currentTopic)) {
            Topic currentTopicName = topicRepository.findTopicByTopicName(currentTopic);

            if(currentTopicName == null) {
                throw new TopicNotFoundException(NO_TOPIC_FOUND_BY_NAME + currentTopic);
            }
            if(topic != null && !currentTopicName.getId().equals(topic.getId())) {
                throw new TopicExistException(TOPIC_ALREADY_EXISTS);
            }
            return currentTopicName;
        } else {
            if(topic != null) {
                throw new TopicExistException(TOPIC_ALREADY_EXISTS);
            }
            return null;
        }
    }

    @Override
    public Topic createTopic(String topicName)
            throws TopicNotFoundException, TopicExistException {

        validateNewTopicExists(EMPTY, topicName);
        Topic topic = new Topic();
        topic.setTopicName(topicName);
        topicRepository.save(topic);

        return topic;
    }

    @Override
    public Topic updateTopic(String currentTopicName,
                             String newTopicName)
            throws TopicNotFoundException, TopicExistException {
        Topic currentTopic = validateNewTopicExists(currentTopicName, newTopicName);
        currentTopic.setTopicName(newTopicName);
        topicRepository.save(currentTopic);

        return currentTopic;
    }

    @Override
    public List<Topic> getAllTopic() {
        return topicRepository.findAll();
    }

    @Override
    public void deleteTopic(Long id) {
        topicRepository.deleteById(id);
    }
}
