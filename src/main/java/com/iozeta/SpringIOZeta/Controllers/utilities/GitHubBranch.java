package com.iozeta.SpringIOZeta.Controllers.utilities;

import lombok.Data;

import java.util.Map;

@Data
public class GitHubBranch {

    String ref;
    String node_id;
    String url;
    Map object;

    public String getSha(){
        return (String) object.get("sha");
    }


}
