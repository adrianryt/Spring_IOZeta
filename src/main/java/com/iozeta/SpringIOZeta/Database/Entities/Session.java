package com.iozeta.SpringIOZeta.Database.Entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="SESSIONS")
@Getter
@Setter
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SESSION_ID")
    private long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "ACCESS_CODE")
    private String accessCode;

    @Column(name = "IS_ACTIVE")
    private boolean active;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TASK_ID")
    private Task task;
}
