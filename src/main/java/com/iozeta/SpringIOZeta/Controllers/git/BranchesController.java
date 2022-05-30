package com.iozeta.SpringIOZeta.Controllers.git;

import com.google.gson.Gson;
import com.iozeta.SpringIOZeta.Controllers.utilities.CommitJson;
import com.iozeta.SpringIOZeta.Controllers.utilities.GitHubBranch;
import com.iozeta.SpringIOZeta.Database.Entities.Lecturer;
import com.iozeta.SpringIOZeta.Database.Repositories.LecturerRepository;
import com.iozeta.SpringIOZeta.Database.Repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

import javax.ws.rs.core.MediaType;
import java.util.*;

@RestController
@RequestMapping(value = "/git")
@RequiredArgsConstructor
public class BranchesController extends AbstractGitController {

    private final LecturerRepository lecturerRepository;
    private final TaskRepository taskRepository;

    @RequestMapping(value = "/branches", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    public ResponseEntity<?> addBranchPost(@RequestBody Map<String, String> body) {

        String lecturerNickname = body.get("lecturer_nickname");
        var lecturer = lecturerRepository.findByGitNick(lecturerNickname);
        if (lecturer.isEmpty()) {
            return new ResponseEntity<>("Wrong lecturer", HttpStatus.BAD_REQUEST);
        }

        String repo = body.get("repo_name");
        String branchName = body.get("branch_name");

        System.out.println(lecturer);

        return addBranch(lecturer.get(), repo, branchName, taskRepository);

    }

    public static ResponseEntity<?> addBranch(Lecturer lecturer, String repo, String branchName, TaskRepository taskRepository){

        String uri = "/repos/" + lecturer.getGitNick() + "/" + repo + "/git/refs";

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


        var lecturer = lecturerRepository.findByGitNick(lecturerNickname);
        if (lecturer.isEmpty()) {
            return new ResponseEntity<>("Wrong lecturer", HttpStatus.BAD_REQUEST);
        }

        String res = requestForCommitToGithub(lecturer.get(), repo, "main");

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    public static String requestForCommitToGithub(Lecturer lecturer, String repo, String branch){
        String uri = "/repos/" + lecturer.getGitNick() + "/" + repo + "/git/matching-refs/heads/" + branch;

        var queryParams = new HashMap<String, Object>();

        var requestBody = prepareGitHubRequest(
                get(), uri, queryParams, lecturer.getGitNick(), lecturer.getGitToken());

        var response = getResponseFromGitHub(requestBody);

        System.out.println(response.block());

        if (Objects.equals(response.block(), "Error response")) {
            throw new RuntimeException("Error while getting response from github");
        } else{
            GitHubBranch[] gitHubBranch = new Gson().fromJson(response.block(), GitHubBranch[].class);
            return gitHubBranch[0].getSha();
        }
    }

    public static String requestForCommitSha(Lecturer lecturer, String repoName, String branchName, String commitMessage) {
        String uri = "/repos/" + lecturer.getGitNick() + "/" + repoName + "/commits";

        HashMap<String, Object> queryParams = new HashMap<>();
        queryParams.put("sha", branchName);

        var requestBody = prepareGitHubRequest(
                get(), uri, queryParams, lecturer.getGitNick(), lecturer.getGitToken()
        );

        var response = getResponseFromGitHub(requestBody);

        if (Objects.equals(response.block(), "Error response")) {
            throw new RuntimeException("Error while getting response from github");
        } else{
            CommitJson[] commits = new Gson().fromJson(response.block(), CommitJson[].class);

            Arrays.stream(commits).forEach(System.out::println);


            CommitJson commitJson = Arrays.stream(commits).filter(commitJson1 -> Objects.equals(commitJson1.getCommit().getMessage(), commitMessage)).min((commitJson1, commitJson2) ->
                    -1 * commitJson1.getCommit().getAuthor().getDate().compareTo(commitJson2.getCommit().getAuthor().getDate()))
                    .get();

            return commitJson.getSha();
        }
    }

}
