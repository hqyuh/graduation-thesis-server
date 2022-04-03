package com.hqh.graduationthesisserver.repository;

import com.hqh.graduationthesisserver.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    Topic findTopicById(Long id);

    Topic findTopicByTopicName(String topicName);

}