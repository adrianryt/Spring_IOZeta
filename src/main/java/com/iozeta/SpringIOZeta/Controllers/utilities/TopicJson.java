package com.iozeta.SpringIOZeta.Controllers.utilities;

import com.iozeta.SpringIOZeta.Database.Entities.Task;
import lombok.Data;

@Data
public class TopicJson {

    private long id;
    private String subject;
    private String title;
    private String repoName;

    public TopicJson(long id, String subject, String title, String repoName) {
        this.id = id;
        this.subject = subject;
        this.title = title;
        this.repoName = repoName;
    }

    public static TopicJson taskToTopicJson(Task task) {
        return new TopicJson(
                task.getId(), task.getSubjectName(), task.getName(), task.getRepoName()
        );
    }

}
