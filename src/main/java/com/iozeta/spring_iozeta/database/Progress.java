package com.iozeta.spring_iozeta.database;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="Progresses")
@Getter
@Setter
public class Progress {

    @Column(name = "is_done")
    private boolean done;
}
