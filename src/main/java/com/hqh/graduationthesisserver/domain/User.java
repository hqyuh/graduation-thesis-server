package com.hqh.graduationthesisserver.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

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

    @Column(name = "first_name", length = 20)
    private String firstName;

    @Column(name = "last_name", length = 20)
    private String lastName;

    @Column(name = "username", length = 50)
    private String username;

    @Column(name = "password", length = 64)
    private String password;

    @Column(name = "email", length = 50)
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

}
