package com.iozeta.SpringIOZeta.Controllers.git;


import com.iozeta.SpringIOZeta.Database.Entities.Lecturer;
import com.iozeta.SpringIOZeta.Database.Entities.Student;
import com.iozeta.SpringIOZeta.Database.Repositories.LecturerRepository;
import com.iozeta.SpringIOZeta.Database.Repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import javax.ws.rs.core.HttpHeaders;

import java.util.Map;
import java.util.Objects;

import static com.iozeta.SpringIOZeta.Controllers.git.RepositoriesController.getResponseFromGitHub;
import static com.iozeta.SpringIOZeta.Controllers.git.RepositoriesController.prepareGitHubRequest;

@RestController
@RequestMapping(value = "/git")
@RequiredArgsConstructor
public class ContributorsController {

    private WebClient webClient = WebClient.create("https://api.github.com");

    private final LecturerRepository lecturerRepository;
    private final StudentRepository studentRepository;

    @RequestMapping(value = "/contributors", method = RequestMethod.POST)
    public ResponseEntity<?> addContributor(@RequestBody Map<String, String> body) {

        String lecturerNickname = body.get("lecturer");
        String repoName = body.get("repo_name");
        String studentNickname = body.get("student_nickname");

        Lecturer lecturer = lecturerRepository.findByGitNick(lecturerNickname);
        if(lecturer == null){
            return new ResponseEntity<>("Lecturer not found. Invalid git nickname.", HttpStatus.BAD_REQUEST);
        }

        Student student = studentRepository.findByGitNick(studentNickname);
        if(student == null){
            return new ResponseEntity<>("Student not found. Invalid git nickname.", HttpStatus.BAD_REQUEST);
        }

        String uri = "/repos" + lecturerNickname + "/" + repoName + "/collaborators" + "/" + studentNickname;

        var requestBody = prepareGitHubRequest(webClient.post(), uri, new LinkedMultiValueMap<>())
            .header(HttpHeaders.CONTENT_LENGTH, "0");

        Mono<String> r = getResponseFromGitHub(requestBody);

        if (Objects.equals(r.block(), "Error response")) {
            return new ResponseEntity<>("Error adding contributor on github", HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>("Contributor added", HttpStatus.CREATED);
        }

    }

}
