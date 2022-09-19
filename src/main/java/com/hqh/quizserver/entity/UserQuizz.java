package com.hqh.quizserver.entity;

import javax.persistence.*;

@Entity
@Table(name = "user_quizz")
public class UserQuizz {

    @EmbeddedId
    private UserQuizzKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("quizzId")
    @JoinColumn(name = "quizz_id")
    private TestQuizz testQuizz;

}
