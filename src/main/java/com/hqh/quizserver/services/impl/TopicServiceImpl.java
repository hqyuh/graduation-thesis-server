package com.hqh.quizserver.services.impl;

import com.hqh.quizserver.dto.TestQuizzResponseDTO;
import com.hqh.quizserver.dto.TopicDTO;
import com.hqh.quizserver.entities.Topic;
import com.hqh.quizserver.exceptions.domain.topic.TopicExistException;
import com.hqh.quizserver.exceptions.domain.topic.TopicNotFoundException;
import com.hqh.quizserver.mapper.TestQuizzMapper;
import com.hqh.quizserver.repositories.TopicRepository;
import com.hqh.quizserver.services.TopicService;
import com.hqh.quizserver.services.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.hqh.quizserver.constant.TopicImplConstant.NO_TOPIC_FOUND_BY_NAME;
import static com.hqh.quizserver.constant.TopicImplConstant.TOPIC_ALREADY_EXISTS;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
public class TopicServiceImpl implements TopicService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final TopicRepository topicRepository;
    private final UserService userService;
    private final TestQuizzMapper testQuizzMapper;

    @Autowired
    public TopicServiceImpl(TopicRepository topicRepository,
                            UserService userService,
                            TestQuizzMapper testQuizzMapper) {
        this.topicRepository = topicRepository;
        this.userService = userService;
        this.testQuizzMapper = testQuizzMapper;
    }

    private String logged = null;

    /**
     * If the new topic exists, throw an exception. If the current topic doesn't exist, throw an exception. If the current
     * topic exists, return it. If the current topic doesn't exist, return null
     *
     * @param currentTopic The topic name that is currently being edited.
     * @param newTopic The new topic name that the user wants to change to.
     * @return The topic object is being returned.
     */
    private Topic validateNewTopicExists(String currentTopic, String newTopic)
            throws TopicNotFoundException, TopicExistException {
        Topic topic = topicRepository.findTopicByTopicName(newTopic);

        logged = userService.getCurrentUser().getUsername();

        if(StringUtils.isNotBlank(currentTopic)) {
            Topic currentTopicName = topicRepository.findTopicByTopicName(currentTopic);

            if(currentTopicName == null) {
                log.error("No topic found by name: {}", currentTopic);
                throw new TopicNotFoundException(NO_TOPIC_FOUND_BY_NAME + currentTopic);
            }
            if(topic != null && !currentTopicName.getId().equals(topic.getId())) {
                log.error("Topic already exists!");
                throw new TopicExistException(TOPIC_ALREADY_EXISTS);
            }
            return currentTopicName;
        } else {
            if(topic != null) {
                log.error("Topic already exists!");
                throw new TopicExistException(TOPIC_ALREADY_EXISTS);
            }
            return null;
        }
    }

    @Override
    public Topic createTopic(String topicName) throws TopicNotFoundException, TopicExistException {

        validateNewTopicExists(EMPTY, topicName);
        Topic topic = new Topic();
        topic.setTopicName(topicName);
        topic.setCreatedAt(new Date());
        topic.setCreatedBy(logged);
        topic.setUpdatedAt(new Date());
        topic.setUpdatedBy(logged);

        topicRepository.save(topic);

        return topic;
    }

    @Override
    public Topic updateTopic(String currentTopicName, String newTopicName)
            throws TopicNotFoundException, TopicExistException {

        Topic currentTopic = validateNewTopicExists(currentTopicName, newTopicName);
        if (currentTopic != null) {
            currentTopic.setTopicName(newTopicName);
            currentTopic.setCreatedAt(new Date());
            currentTopic.setCreatedBy(logged);
            currentTopic.setUpdatedAt(new Date());
            currentTopic.setUpdatedBy(logged);

            topicRepository.save(currentTopic);
        }
        return currentTopic;
    }

    /**
     * It returns a list of TopicDTO objects.
     *
     * @return A list of TopicDTO objects
     */
    @Override
    public List<TopicDTO> getAllTopic() {
        log.info("Get All Topic ::");
        List<Topic> topicList = topicRepository.findAll();
        List<TopicDTO> topicDTOList = new ArrayList<>();

        for (Topic topic : topicList) {
            TopicDTO topicDTO = new TopicDTO();
            topicDTO.setId(topic.getId());
            topicDTO.setName(topic.getTopicName());

            // get quiz from topic
            List<TestQuizzResponseDTO> testQuizzResponseDTOList = topic
                    .getTestQuizz()
                    .stream()
                    .map(testQuizzMapper::convertQuizzToQuizzResponseDTO)
                    .collect(Collectors.toList());

            topicDTO.setQuizzResponseDTOList(testQuizzResponseDTOList.isEmpty() ? null : testQuizzResponseDTOList);

            topicDTOList.add(topicDTO);
        }
        log.info("End get All Topic ::");
        return topicDTOList;
    }

    @Override
    public void deleteTopic(Long id) {
        log.info("{} deleted the test with id: {}", logged, id);
        topicRepository.deleteById(id);
    }

    @Override
    public TopicDTO getTopicByID(Long id) {
        log.info("Get Topic by ID");
        Topic topic = topicRepository.findTopicById(id);

        List<TestQuizzResponseDTO> testQuizzResponseDTOList = topic
                .getTestQuizz()
                .stream()
                .map(testQuizzMapper::convertQuizzToQuizzResponseDTO)
                .collect(Collectors.toList());

        return TopicDTO
                .builder()
                .id(topic.getId())
                .name(topic.getTopicName())
                .quizzResponseDTOList(testQuizzResponseDTOList.isEmpty() ? null : testQuizzResponseDTOList)
                .build();
    }
}
