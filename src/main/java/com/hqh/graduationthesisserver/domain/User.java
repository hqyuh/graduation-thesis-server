package com.hqh.graduationthesisserver.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static com.hqh.graduationthesisserver.constant.PatternConstant.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "user")
@Entity
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(name = "first_name", length = 20, nullable = false)
    @NotBlank(message = "First name is mandatory")
    @Pattern(regexp = NAME_PATTERN, message = "First name invalidate")
    private String firstName;

    @Column(name = "last_name", length = 20)
    @NotBlank(message = "Last name is mandatory")
    @Pattern(regexp = NAME_PATTERN, message = "Last name invalidate")
    private String lastName;

    @Column(name = "username", length = 50, nullable = false)
    @NotBlank(message = "Username is mandatory")
    @Pattern(regexp = USERNAME_PATTERN, message = "Username invalidate")
    private String username;

    @Column(name = "password", length = 64, nullable = false)
    @NotBlank(message = "Password is mandatory")
    @Pattern(regexp = PASSWORD_PATTERN, message = "Password invalidate")
    private String password;

    @Column(name = "email", length = 50, nullable = false)
    @NotBlank(message = "Email is mandatory")
    @Pattern(regexp = EMAIL_PATTERN, message = "Email invalidate")
    private String email;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "last_login")
    private Date lastLogin;

    @Column(name = "last_login_date_display")
    private Date lastLoginDateDisplay;

    @Column(name = "join_date")
    private Date joinDate;

    private String roles;
    private String[] authorities;
    private boolean isActive;
    private boolean isNotLocked;

    @ManyToMany(mappedBy = "users")
    @JsonIgnore
    private List<TestQuizz> quizzes;

    public User(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}
