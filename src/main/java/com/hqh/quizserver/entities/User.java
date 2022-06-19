package com.hqh.quizserver.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hqh.quizserver.enumeration.AuthenticationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static com.hqh.quizserver.constant.PatternConstant.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "tbl_user")
@Entity
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(name = "first_name", length = 20, nullable = false)
    @NotBlank(message = "First name is mandatory")
    @NotNull
    @Pattern(regexp = NAME_PATTERN, message = "First name invalidate")
    private String firstName;

    @Column(name = "last_name", length = 20)
    @NotBlank(message = "Last name is mandatory")
    @NotNull
    @Pattern(regexp = NAME_PATTERN, message = "Last name invalidate")
    private String lastName;

    @Column(name = "username", length = 50, nullable = false)
    @NotBlank(message = "Username is mandatory")
    @NotNull
    @Pattern(regexp = USERNAME_PATTERN, message = "Username invalidate")
    private String username;

    @Column(name = "password", length = 64, nullable = false)
    @NotBlank(message = "Password is mandatory")
    @NotNull
    @Pattern(regexp = PASSWORD_PATTERN, message = "Password invalidate")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "email", length = 50, nullable = false)
    @NotBlank(message = "Email is mandatory")
    @NotNull
    @Pattern(regexp = EMAIL_PATTERN, message = "Email invalidate")
    private String email;

    @Column(name = "phone_number", length = 12)
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private Timestamp dateOfBirth;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_type", length = 25)
    private AuthenticationType authType;

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
