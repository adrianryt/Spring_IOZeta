package com.iozeta.SpringIOZeta.Controllers.git;

import com.iozeta.SpringIOZeta.Database.Entities.Lecturer;
import com.iozeta.SpringIOZeta.Database.Repositories.LecturerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.Objects;


@RestController
@RequestMapping(value = "/git")
@RequiredArgsConstructor
public class RepositoriesController extends AbstractGitController {

    private final LecturerRepository lecturerRepository;

    @RequestMapping(value = "/repo", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    public ResponseEntity<?> createRepositoryPost(@RequestBody Map<String, String> body) {

        String nickname = body.get("lecturer_nickname");
        String repoName = body.get("repo_name");

        var lecturer = lecturerRepository.findByGitNick(nickname);
        if (lecturer.isEmpty()) {
            return new ResponseEntity<>("Lecturer not found. Invalid git nickname.", HttpStatus.BAD_REQUEST);
        }

        return createRepository(repoName, lecturer.get());
    }


    public static ResponseEntity<?> createRepository(String repoName, Lecturer lecturer){

        String uri ="/user/repos";
        String gitHubBody = "{\n \"name\": \"" + repoName + "\"\n}";

        var headersSpec = prepareGitHubRequest(
                post(), uri, gitHubBody, lecturer.getGitNick(), lecturer.getGitToken());

        Mono<String> r = getResponseFromGitHub(headersSpec);

        if (Objects.equals(r.block(), "Error response")) {
            return new ResponseEntity<>("Error creating repo on github", HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>("Repo created", HttpStatus.CREATED);
        }

    }

}
