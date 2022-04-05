package com.iozeta.spring_iozeta.database;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="Students")
@Getter
@Setter
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private long id;

    @Column(name = "git_nick")
    private String gitNick;

    @Column(name = "branch_name")
    private String branchName;

    @OneToMany(mappedBy = "student")
    private Set<Progress> progresses;
}
