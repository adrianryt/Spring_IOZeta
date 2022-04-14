package com.iozeta.SpringIOZeta.Database.Entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="CHECKPOINTS")
@Getter
@Setter
public class Checkpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHECKPOINT_ID")
    private long id;

    @Column(name = "NUMBER")
    private int number;

    @Column(name = "CONTENT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "TASK_ID")
    private Task task;

    @OneToMany(mappedBy = "checkpoint")
    private Set<Progress> progresses = new HashSet<>();

    public void addProgress(Progress progress) {
        progresses.add(progress);
    }
}
