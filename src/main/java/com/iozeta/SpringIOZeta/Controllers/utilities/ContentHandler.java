package com.iozeta.SpringIOZeta.Controllers.utilities;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.Arrays;

@Getter
public class ContentHandler {
    @NotNull
    private String title;
    @NotNull
    private String description;
    @NotNull
    private String[] commands;

    @Override
    public String toString() {
        return "ContentHandler{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", commands=" + Arrays.toString(commands) +
                '}';
    }
}
