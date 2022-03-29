package com.hqh.graduationthesisserver.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class UserQuizzKey implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "quizz_id")
    private Long quizzId;

}
