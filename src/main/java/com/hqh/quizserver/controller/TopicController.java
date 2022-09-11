package com.hqh.quizserver.controller;

import com.hqh.quizserver.dto.TopicDTO;
import com.hqh.quizserver.entities.ApiResponse;
import com.hqh.quizserver.exceptions.domain.topic.TopicExistException;
import com.hqh.quizserver.exceptions.domain.topic.TopicNotFoundException;
import com.hqh.quizserver.services.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.hqh.quizserver.constant.MessageTypeConstant.SUCCESS;
import static com.hqh.quizserver.constant.TopicImplConstant.*;
import static com.hqh.quizserver.utils.ResponseUtils.response;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = { "/topic" })
public class TopicController {

    private final TopicService topicService;

    @Autowired
    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createTopic(@RequestParam("topicName") String topicName)
            throws TopicNotFoundException, TopicExistException {
        topicService.createTopic(topicName);
        return response(CREATED, SUCCESS, ADD_TOPIC_SUCCESS);
    }

    @PatchMapping("/update")
    public ResponseEntity<ApiResponse> createTopic(@RequestParam("currentTopicName") String currentTopicName,
                                                   @RequestParam("topicName") String topicName)
            throws TopicNotFoundException, TopicExistException {
        topicService.updateTopic(currentTopicName, topicName);
        return response(OK, SUCCESS, UPDATE_TOPIC_SUCCESS);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTopic(@PathVariable("id") Long id) {
        topicService.deleteTopic(id);

        return response(OK, SUCCESS, DELETED_TOPIC_SUCCESSFULLY);
    }

    @GetMapping("/list")
    public ResponseEntity<List<TopicDTO>> getAllTopic() {
        List<TopicDTO> topicDTOList = topicService.getAllTopic();

        return new ResponseEntity<>(topicDTOList, OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<TopicDTO> getTopicByID(@PathVariable(name = "id") Long id) {
        TopicDTO topicDTO = topicService.getTopicByID(id);

        return new ResponseEntity<>(topicDTO, OK);
    }

}
