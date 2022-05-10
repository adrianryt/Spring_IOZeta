package com.iozeta.SpringIOZeta.Database.Entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="LECTURERS")
@Getter
@Setter
public class Lecturer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LECTURER_ID")
    private long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "SURNAME")
    private String surname;

    @Column(name = "GIT_NICK")
    private String gitNick;

    @Column(name = "GIT_TOKEN")
    private String gitToken;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Subject> subjects = new HashSet<>();

    @Column
    private String password;

    public void addSubject(Subject subject) {
        subjects.add(subject);
    }

    public List<String> getRoles(){
        List<String> roles = new ArrayList<>();
        roles.add("LECTURER");
        return roles;
    }
}
