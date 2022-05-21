package com.iozeta.SpringIOZeta.Controllers.utilities;

import com.iozeta.SpringIOZeta.Database.Entities.Lecturer;
import lombok.Data;

import java.util.List;

@Data
public class SubjectJson {

    private Long id;
    private String name;
    private Lecturer lecturer;
    private List<TopicJson> topics;

    public SubjectJson(Long id, String name, Lecturer lecturer, List<TopicJson> topics) {
        this.id = id;
        this.name = name;
        this.lecturer = lecturer;
        this.topics = topics;
    }
}
