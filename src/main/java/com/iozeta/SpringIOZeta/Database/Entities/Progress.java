package com.iozeta.SpringIOZeta.Database.Entities;

import com.iozeta.SpringIOZeta.Database.Status;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="PROGRESSES")
@Getter
@Setter
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROGRESS_ID")
    private long id;

    @ManyToOne
    @JoinColumn(name = "STUDENT_ID")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "SESSION_ID")
    private Session session;

    @ManyToOne
    @JoinColumn(name = "CHECKPOINT_ID")
    private Checkpoint checkpoint;

    @Column(name = "LINK_TO_COMMIT")
    private String linkToCommit;

    @Column(name = "STATUS")
    private Status status;
}
