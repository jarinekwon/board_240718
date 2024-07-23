package com.boot.board_240718.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
//Entity가 다른 Entity의 연관관게가 있는 상태에서 둘 다 Data 어노테이션이 붙어있을 경우
//Lombok에서 생성하는 equals, hashcode 혹은 toString 메서드가 서로를 순환 참조하는 문제가 발생
//@Data
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String username;
    private String password;
    private Boolean enabled;

    @ManyToMany
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
//    Set<Course> likedCourses;
//    private List<Role> roles;
    @JsonIgnore
    private List<Role> roles = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Board> boards = new ArrayList<>();
}
