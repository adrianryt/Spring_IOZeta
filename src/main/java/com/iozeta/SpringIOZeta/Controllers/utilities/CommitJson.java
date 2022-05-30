package com.iozeta.SpringIOZeta.Controllers.utilities;

import lombok.Data;

import java.util.Date;

@Data
public class CommitJson{

    public record Author(Date date){};

    public record CommitDetails(String message, Author author){};

    private String sha;
    private CommitDetails commit;
}
