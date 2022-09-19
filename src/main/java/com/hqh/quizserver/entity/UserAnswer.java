package com.hqh.quizserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hqh.quizserver.entity.base.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user_answer")
public class UserAnswer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "quizz_id")
    private TestQuizz testQuizz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "question_id")
    private Question question;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_selected", length = 5)
    private String isSelected;

    @Column(name = "short_answer")
    private String shortAnswer;

    @Column(name = "is_correct", columnDefinition = "boolean default false")
    private boolean isCorrect;

    @Column(name = "is_used", columnDefinition = "boolean default true")
    private boolean isUsed;
}
