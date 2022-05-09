package com.iozeta.SpringIOZeta.Controllers.git;

import com.iozeta.SpringIOZeta.Database.Entities.Lecturer;
import com.iozeta.SpringIOZeta.Database.Repositories.LecturerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import javax.ws.rs.core.MediaType;

import java.util.Map;
import java.util.Objects;

import static com.iozeta.SpringIOZeta.Controllers.git.RepositoriesController.getResponseFromGitHub;
import static com.iozeta.SpringIOZeta.Controllers.git.RepositoriesController.prepareGitHubRequest;

@RestController
@RequestMapping(value = "/git")
@RequiredArgsConstructor
public class BranchesController {

    private WebClient webClient = WebClient.create("https://api.github.com");

    private final LecturerRepository lecturerRepository;

    @RequestMapping(value = "/branches", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    public ResponseEntity<?> addBranch(@RequestBody Map<String, String> body){

        String lecturerNickname = body.get("lecturer_nickname");
        Lecturer lecturer = lecturerRepository.findByGitNick(lecturerNickname);
        if(lecturer == null){
            return new ResponseEntity<>("Wrong lecturer", HttpStatus.BAD_REQUEST);
        }

        String repo = body.get("repo_name");
        String taskBranchName = body.get("task_branch_name");

        String uri = "/repos" + lecturerNickname + "/" + repo + "/git/refs";

        String commitToBranchFromSha = "TO_DO"; // get from database

        var gitHubRequestBody = new LinkedMultiValueMap<String, String >();
        gitHubRequestBody.add("ref", "refs/heads/" + taskBranchName);
        gitHubRequestBody.add("sha", commitToBranchFromSha);

        var requestBody = prepareGitHubRequest(webClient.post(), uri, gitHubRequestBody,
                lecturer.getGitNick(), lecturer.getGitToken());

        var response = getResponseFromGitHub(requestBody);

        if (Objects.equals(response.block(), "Error response")) {
            return new ResponseEntity<>("Error creating branch on github", HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>("Branch created", HttpStatus.CREATED);
        }

    }

}