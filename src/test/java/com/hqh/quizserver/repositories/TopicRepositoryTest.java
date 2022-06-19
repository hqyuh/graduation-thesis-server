package com.hqh.quizserver.repositories;

import com.hqh.quizserver.entities.Topic;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;


import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class TopicRepositoryTest {

    private final TopicRepository topicRepository;

    @Autowired
    TopicRepositoryTest(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @Test
    public void testCreateTopic() {
        Topic topic = new Topic("Programming Language");

        Topic savedTopic = topicRepository.save(topic);
        assertThat(savedTopic.getId()).isGreaterThan(0);
    }

    @Test
    public void testUpdateTopic() {
        Topic topic = topicRepository.findById(2L).get();
        topic.setTopicName("english");

        topicRepository.save(topic);
    }

    @Test
    public void testDeleteTopic() {
        Long topicId = 2L;
        topicRepository.deleteById(topicId);
    }

}