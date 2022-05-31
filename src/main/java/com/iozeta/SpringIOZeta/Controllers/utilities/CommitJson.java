package com.iozeta.SpringIOZeta.Controllers.utilities;

import lombok.Data;

import java.util.Date;

@Data
public class CommitJson{

    private String sha;
    private CommitDetails commit;
}
