package com.iozeta.SpringIOZeta.Controllers.utilities;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class ContentHandler {
    @NotNull
    private String title;
    @NotNull
    private String description;

    @Override
    public String toString() {
        return "ContentHandler{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
