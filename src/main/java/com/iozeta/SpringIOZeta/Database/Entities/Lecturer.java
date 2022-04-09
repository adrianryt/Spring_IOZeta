package com.iozeta.SpringIOZeta.Database.Entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="Lecturers")
@Getter
@Setter
public class Lecturer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecturer_id")
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "git_nick")
    private String gitNick;

    @Column(name = "git_token")
    private String gitToken;

    @OneToMany(mappedBy = "lecturer")
    private Set<Subject> subjects = new HashSet<>();
}
