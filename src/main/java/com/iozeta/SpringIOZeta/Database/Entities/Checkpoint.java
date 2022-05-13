package com.iozeta.SpringIOZeta.Database.Entities;

import com.iozeta.SpringIOZeta.Database.Entities.utilities.Content;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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

    @OneToOne
    @JoinColumn(name = "CONTENT_ID")
    private Content content;

    @ManyToOne
    @JoinColumn(name = "TASK_ID")
    private Task task;
}
