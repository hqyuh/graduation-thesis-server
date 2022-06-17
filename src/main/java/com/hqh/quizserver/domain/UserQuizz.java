package com.hqh.quizserver.domain;

import javax.persistence.*;

@Entity
@Table(name = "tbl_user_quizz")
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
