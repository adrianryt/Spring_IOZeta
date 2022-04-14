package com.iozeta.SpringIOZeta.Database.Entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="SUBJECTS")
@Getter
@Setter
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SUBJECT_ID")
    private long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "REPOSITORY_NAME")
    private String repoName;

    @ManyToOne
    @JoinColumn(name = "LECTURER_ID")
    private Lecturer lecturer;

    @OneToMany(mappedBy = "subject")
    private Set<Task> tasks = new HashSet<>();

    public void addTask(Task task) {
        tasks.add(task);
    }
}