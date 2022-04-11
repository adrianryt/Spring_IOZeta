package com.iozeta.SpringIOZeta.Database.Entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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

    @Column(name = "BRANCH_NAME")
    private String branchName;

    @ManyToOne
    @JoinColumn(name = "SUBJECT_ID")
    private Subject subject;

    @OneToMany(mappedBy = "task")
    private Set<Checkpoint> checkpoints = new HashSet<>();

    @OneToMany(mappedBy = "task")
    private Set<Session> sessions = new HashSet<>();

    public void addCheckpoint(Checkpoint checkpoint) {
        checkpoints.add(checkpoint);
    }

    public void addSession(Session session) {
        sessions.add(session);
    }
}
