package com.hqh.quizserver.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "test_quizz")
@Entity
public class TestQuizz extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(name = "test_name", nullable = false, length = 45)
    private String testName;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd-MM-yyyy hh:mm:ss",
            timezone = "Asia/Ho_Chi_Minh"
    )
    @Column(name = "date_created")
    private Instant dateCreated;

    @Column(name = "exam_time", nullable = false)
    private Integer examTime;

    @Column(name = "is_start")
    private Timestamp isStart;

    @Column(name = "is_end")
    private Timestamp isEnd;

    @Column(name = "activation_code", nullable = false, length = 7)
    private String activationCode;

    @Column(name = "is_status")
    private boolean isStatus;

    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name = "user_quizz",
            joinColumns = @JoinColumn(name = "quizz_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "testQuizz")
    private List<Question> questions;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    @JsonIgnore
    private Topic topic;

    public void addUser(User user) {
        this.users.add(user);
    }

    public TestQuizz(String testName, String activationCode) {
        this.testName = testName;
        this.activationCode = activationCode;
    }

    @Override
    public String toString() {
        return "TestQuizz{" +
                "id=" + id +
                ", testName='" + testName + '\'' +
                ", dateCreated=" + dateCreated +
                ", examTime=" + examTime +
                ", isStart=" + isStart +
                ", isEnd=" + isEnd +
                ", activationCode='" + activationCode + '\'' +
                ", questions=" + questions +
                '}';
    }
}
