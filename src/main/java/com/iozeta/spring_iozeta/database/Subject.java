package com.iozeta.spring_iozeta.database;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="Subjects")
@Getter
@Setter
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "repository_name")
    private String repoName;

    @ManyToOne
    @JoinColumn(name = "lecturer_id")
    private Lecturer lecturer;

    @OneToMany(mappedBy = "subject")
    private Set<Task> tasks = new HashSet<>();
}