package com.iozeta.SpringIOZeta.Database.Entities.utilities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="Contents")
@Getter
@Setter
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONTENT_ID")
    private long id;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "DESCRIPTION")
    private String description;
}
