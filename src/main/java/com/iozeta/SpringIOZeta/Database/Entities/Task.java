package com.iozeta.SpringIOZeta.Database.Entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="TASKS")
@Getter
@Setter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TASK_ID")
    private long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "REPOSITORY_NAME")
    private String repoName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SUBJECT_ID")
    private Subject subject;

    @Column(name="COMMIT_SHA")
    private String commitSha;
}
