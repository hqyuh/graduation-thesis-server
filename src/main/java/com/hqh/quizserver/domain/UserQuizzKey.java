package com.hqh.quizserver.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class UserQuizzKey implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "quizz_id")
    private Long quizzId;

}
