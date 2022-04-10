package com.iozeta.SpringIOZeta.Database.Entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="Checkpoints")
@Getter
@Setter
public class Checkpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checkpoint_id")
    private long id;

    @Column(name = "number")
    private int number;

    @Column(name = "content")
    private String content;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @OneToMany(mappedBy = "checkpoint")
    private Set<Progress> progresses = new HashSet<>();

    public void addProgress(Progress progress) {
        progresses.add(progress);
    }
}