package com.iozeta.SpringIOZeta.Controllers.utilities;

import javax.validation.constraints.NotNull;

public class StudentSessionForm {

    @NotNull
    private String githubUsername;
    @NotNull
    private String code;

    public String getGithubUsername() {
        return githubUsername;
    }

    public String getCode() {
        return code;
    }
}
