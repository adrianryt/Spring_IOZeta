package com.iozeta.spring_iozeta.database;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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
}
