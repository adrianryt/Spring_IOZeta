package com.iozeta.spring_iozeta.database;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="Sessions")
@Getter
@Setter
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "access_code")
    private String access_code;

    @Column(name = "is_active")
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;
}
