package com.iozeta.SpringIOZeta.Controllers.utilities;

import com.iozeta.SpringIOZeta.Database.Entities.utilities.Content;

import javax.validation.constraints.NotNull;
import java.util.Arrays;

public class NewTaskForm {

    @NotNull
    private String taskName;

    @NotNull
    private String repositoryLink;

    @NotNull
    private String subject;

    @NotNull
    private String repositoryName;

    @NotNull
    private String lecturerGitNick;

    private ContentHandler[] checkpointsContent;

    public String getTaskName() {
        return taskName;
    }

    public String getRepositoryLink() {
        return repositoryLink;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public String getSubject() {
        return subject;
    }

    public String getLecturerGitNick() {
        return lecturerGitNick;
    }

    public ContentHandler[] getCheckpointsContent() {
        return checkpointsContent;
    }

    @Override
    public String toString() {
        return "NewTaskForm{" +
                "taskName='" + taskName + '\'' +
                ", repositoryLink='" + repositoryLink + '\'' +
                ", subject='" + subject + '\'' +
                ", repositoryName='" + repositoryName + '\'' +
                ", checkpointsContent=" + Arrays.toString(checkpointsContent) +
                '}';
    }
}
