package com.iozeta.SpringIOZeta.Controllers.git;


import com.iozeta.SpringIOZeta.Database.Entities.Lecturer;
import com.iozeta.SpringIOZeta.Database.Entities.Student;
import com.iozeta.SpringIOZeta.Database.Repositories.LecturerRepository;
import com.iozeta.SpringIOZeta.Database.Repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(value = "/git")
@RequiredArgsConstructor
public class ContributorsController extends AbstractGitController {

    private final LecturerRepository lecturerRepository;
    private final StudentRepository studentRepository;

    @RequestMapping(value = "/contributors", method = RequestMethod.POST)
    public ResponseEntity<?> addContributorPost(@RequestBody Map<String, String> body) {

        String lecturerNickname = body.get("lecturer_nickname");
        String repoName = body.get("repo_name");
        String studentNickname = body.get("student_nickname");

        var lecturer = lecturerRepository.findByGitNick(lecturerNickname);
        if(lecturer.isEmpty()){
            return new ResponseEntity<>("Lecturer not found. Invalid git nickname.", HttpStatus.BAD_REQUEST);
        }

        List<Student> students = studentRepository.findStudentsByGitNick(studentNickname);
        if(students.size() == 0){
            return new ResponseEntity<>("Student not found. Invalid git nickname.", HttpStatus.BAD_REQUEST);
        }
        return addContributor(lecturer.get(), repoName, studentNickname);
    }

    public static ResponseEntity<?> addContributor(Lecturer lecturer, String repoName, String studentNickname){
        String uri = "/repos/" + lecturer.getGitNick() + "/" + repoName + "/collaborators/" + studentNickname;

        var requestBody = prepareGitHubRequest(
                put(), uri, "", lecturer.getGitNick(),
                lecturer.getGitToken());

        Mono<String> r = getResponseFromGitHub(requestBody);

        if (Objects.equals(r.block(), "Error response")) {
            return new ResponseEntity<>("Error adding contributor on github", HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>("Contributor added", HttpStatus.CREATED);
        }
    }
}
