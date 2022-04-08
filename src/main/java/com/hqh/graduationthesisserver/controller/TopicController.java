package com.hqh.graduationthesisserver.controller;

import com.hqh.graduationthesisserver.domain.HttpResponse;
import com.hqh.graduationthesisserver.domain.Topic;
import com.hqh.graduationthesisserver.exception.domain.topic.TopicExistException;
import com.hqh.graduationthesisserver.exception.domain.topic.TopicNotFoundException;
import com.hqh.graduationthesisserver.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.hqh.graduationthesisserver.constant.MessageTypeConstant.SUCCESS;
import static com.hqh.graduationthesisserver.constant.TopicConstant.*;
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

    @PostMapping("/add")
    public ResponseEntity<HttpResponse> createTopic(@RequestParam("topicName") String topicName)
            throws TopicNotFoundException, TopicExistException {
        Topic topic = topicService.createTopic(topicName);
        return response(CREATED, SUCCESS, ADD_TOPIC_SUCCESS);
    }

    @PatchMapping("/update")
    public ResponseEntity<HttpResponse> createTopic(@RequestParam("currentTopicName") String currentTopicName,
                                                    @RequestParam("topicName") String topicName)
            throws TopicNotFoundException, TopicExistException {
        Topic topic = topicService.updateTopic(currentTopicName, topicName);
        return response(OK, SUCCESS, UPDATE_TOPIC_SUCCESS);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTopic(@PathVariable("id") Long id) {
        topicService.deleteTopic(id);

        return response(OK, SUCCESS, DELETED_TOPIC_SUCCESSFULLY);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Topic>> getAllTopic() {
        List<Topic> topicList = topicService.getAllTopic();

        return new ResponseEntity<>(topicList, OK);
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String type, String message){
        HttpResponse body = new HttpResponse(httpStatus.value(), httpStatus, type.toUpperCase(),
                httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase());
        return new ResponseEntity<>(body, httpStatus);
    }

}
