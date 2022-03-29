package com.hqh.graduationthesisserver.domain;

import javax.persistence.*;

@Entity
@Table(name = "user_quizz")
public class UserQuizz {

    @EmbeddedId
    private UserQuizzKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("quizzId")
    @JoinColumn(name = "quizz_id")
    private TestQuizz testQuizz;

}
