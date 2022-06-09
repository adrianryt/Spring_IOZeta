package com.iozeta.SpringIOZeta.api;

import com.iozeta.SpringIOZeta.Database.Entities.Lecturer;
import com.iozeta.SpringIOZeta.Database.Services.LecturerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.iozeta.SpringIOZeta.Controllers.git.AbstractGitController.prepareGitHubRequest;
import static com.iozeta.SpringIOZeta.Controllers.git.AbstractGitController.get;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LecturerController {
    private final LecturerService lecturerService;

    @GetMapping("/lecturers")
    public ResponseEntity<List<Lecturer>> getUsers(){
        return ResponseEntity.ok().body(lecturerService.getLecturers());
    }

    @PostMapping("/lecturer/save")
    public ResponseEntity<?> saveUser(@RequestBody Lecturer lecturer){

        var githubRequest = prepareGitHubRequest(get(),
                "/user",
                new HashMap<>(), lecturer.getGitNick(), lecturer.getGitToken());

        var monoResponse = githubRequest.exchangeToMono(response -> {
            if(response.headers().header("X-OAuth-Scopes").size() == 0){
                return Mono.just("token error");
            }
            return response.bodyToMono(String.class);
        });
        if(Objects.equals(monoResponse.block(), "token error")){
            return ResponseEntity.internalServerError().body("Provided token is invalid. Check if the authorization is sufficient.");
        }

        if (lecturerService.getLecturers().stream().anyMatch(lecturer1 ->
                lecturer1.getGitNick().equals(lecturer.getGitNick()))){
            return ResponseEntity.internalServerError().body("Github username already in use");
        }
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/lecturer/save").toUriString());
        return ResponseEntity.created(uri).body(lecturerService.saveLecturer(lecturer));
    }
}
