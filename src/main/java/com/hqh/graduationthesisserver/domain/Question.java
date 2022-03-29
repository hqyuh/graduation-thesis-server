package com.hqh.graduationthesisserver.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(name = "topic_question", nullable = false)
    private String topicQuestion;

    @Column(name = "question_image_url")
    private String questionImageUrl;

    @Column(name = "answer_a", nullable = false)
    private String answerA;

    @Column(name = "answer_b", nullable = false)
    private String answerB;

    @Column(name = "answer_c")
    private String answerC;

    @Column(name = "answer_d")
    private String answerD;

    @Column(name = "correct_result", length = 3)
    private String correctResult;

    @Column(name = "mark", nullable = false)
    private float mark;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd-MM-yyyy hh:mm:ss", timezone = "Asia/Ho_Chi_Minh"
    )
    @Column(name = "date_created")
    private Instant dateCreated;

    @ManyToOne
    @JoinColumn(name = "quizz_id")
    @JsonIgnore
    private TestQuizz testQuizz;

    public Question(String topicQuestion,
                    String questionImageUrl,
                    String answerA,
                    String answerB,
                    String answerC,
                    String answerD,
                    String correctResult,
                    float mark,
                    Instant dateCreated,
                    TestQuizz testQuizz) {
        this.topicQuestion = topicQuestion;
        this.questionImageUrl = questionImageUrl;
        this.answerA = answerA;
        this.answerB = answerB;
        this.answerC = answerC;
        this.answerD = answerD;
        this.correctResult = correctResult;
        this.mark = mark;
        this.dateCreated = dateCreated;
        this.testQuizz = testQuizz;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", topicQuestion='" + topicQuestion + '\'' +
                ", answerA='" + answerA + '\'' +
                ", answerB='" + answerB + '\'' +
                ", answerC='" + answerC + '\'' +
                ", answerD='" + answerD + '\'' +
                ", correctResult='" + correctResult + '\'' +
                ", mark=" + mark +
                ", dateCreated=" + dateCreated +
                '}';
    }
}
