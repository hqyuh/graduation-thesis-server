package com.hqh.graduationthesisserver.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "report_and_suggestions")
public class ReportAndSuggestions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String comment;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User user;

}