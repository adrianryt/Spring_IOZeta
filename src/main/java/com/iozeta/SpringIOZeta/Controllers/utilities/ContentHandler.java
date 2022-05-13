package com.iozeta.SpringIOZeta.Controllers.utilities;

import javax.validation.constraints.NotNull;
import java.util.Arrays;

public class ContentHandler {
    @NotNull
    private String title;
    @NotNull
    private String description;
    @NotNull
    private String[] commands;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String[] getCommands() {
        return commands;
    }

    @Override
    public String toString() {
        return "ContentHandler{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", commands=" + Arrays.toString(commands) +
                '}';
    }
}
