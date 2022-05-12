package com.iozeta.SpringIOZeta.Controllers.git;

import com.google.gson.Gson;
import com.iozeta.SpringIOZeta.Controllers.utilities.GitHubBranch;
import com.iozeta.SpringIOZeta.Database.Entities.Lecturer;
import com.iozeta.SpringIOZeta.Database.Repositories.LecturerRepository;
import com.iozeta.SpringIOZeta.Database.Repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.iozeta.SpringIOZeta.Controllers.git.RepositoriesController.getResponseFromGitHub;
import static com.iozeta.SpringIOZeta.Controllers.git.RepositoriesController.prepareGitHubRequest;

@RestController
@RequestMapping(value = "/git")
@RequiredArgsConstructor
public class BranchesController extends AbstractGitController {

    private final LecturerRepository lecturerRepository;
    private final TaskRepository taskRepository;

    @RequestMapping(value = "/branches", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    public ResponseEntity<?> addBranch(@RequestBody Map<String, String> body) {

        String lecturerNickname = body.get("lecturer_nickname");
        Lecturer lecturer = lecturerRepository.findByGitNick(lecturerNickname);
        if (lecturer == null) {
            return new ResponseEntity<>("Wrong lecturer", HttpStatus.BAD_REQUEST);
        }

        String repo = body.get("repo_name");
        String branchName = body.get("branch_name");

        System.out.println(lecturer);

        String uri = "/repos/" + lecturerNickname + "/" + repo + "/git/refs";

        String commitToBranchFromSha = taskRepository.findByRepoName(repo).getCommitSha();
        System.out.println(commitToBranchFromSha);

        var gitHubRequestBody = "{\n" +
                "\"ref\": \"refs/heads/" + branchName + "\",\n" +
                "\"sha\": \"" + commitToBranchFromSha + "\"\n}";

        var requestBody = prepareGitHubRequest(post(), uri, gitHubRequestBody,
                lecturer.getGitNick(), lecturer.getGitToken());

        var response = getResponseFromGitHub(requestBody);

        if (Objects.equals(response.block(), "Error response")) {
            return new ResponseEntity<>("Error creating branch on github", HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>("Branch " + branchName + " created on repo " + repo, HttpStatus.CREATED);
        }

    }

    // just for debug
    ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {

            StringBuilder sb = new StringBuilder("Request: \n");
            sb.append(request.method());
            sb.append(" ").append(request.url()).append("\n");

            request
                    .headers()
                    .forEach((name, values) -> values.forEach(value -> sb.append(name).append(":").append(value)));
            sb.append("\nattrs:\n");
            request
                    .attributes()
                    .forEach((name, value) -> sb.append(name).append(":").append(value.toString()));

            System.out.println(sb);

            return Mono.just(request);
        });
    }

    @RequestMapping(value = "/branches", method = RequestMethod.GET)
    public ResponseEntity<?> getBranchCommitSha(@RequestParam("nick") String lecturerNickname, @RequestParam("repo") String repo) {


        Lecturer lecturer = lecturerRepository.findByGitNick(lecturerNickname);
        if (lecturer == null) {
            return new ResponseEntity<>("Wrong lecturer", HttpStatus.BAD_REQUEST);
        }

        String uri = "/repos/" + lecturerNickname + "/" + repo + "/git/matching-refs/heads";

        var queryParams = new HashMap<String, Object>();
        queryParams.put("per_page", 100);
        queryParams.put("page", 1);

        var requestBody = prepareGitHubRequest(
                get(), uri, queryParams, lecturer.getGitNick(), lecturer.getGitToken());

        var response = getResponseFromGitHub(requestBody);

        if (Objects.equals(response.block(), "Error response")) {
            return new ResponseEntity<>("Error checking git sha", HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            Gson gson = new Gson();

            GitHubBranch[] responseAsArray = gson.fromJson(response.block(), GitHubBranch[].class);
            String res = "";
            try {
                res = responseAsArray[0].getSha();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return new ResponseEntity<>(res, HttpStatus.OK);

        }

    }


}