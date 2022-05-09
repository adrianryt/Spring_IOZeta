package com.iozeta.SpringIOZeta.Controllers.git;

import com.iozeta.SpringIOZeta.Database.Entities.Lecturer;
import com.iozeta.SpringIOZeta.Database.Repositories.LecturerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.UriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import reactor.core.publisher.Mono;


import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.net.MulticastSocket;
import java.util.Map;
import java.util.Objects;


@RestController
@RequestMapping(value = "/git")
@RequiredArgsConstructor
public class RepositoriesController {

    private final LecturerRepository lecturerRepository;

    private final WebClient webClient = WebClient.create("https://api.github.com");

    @RequestMapping(value = "/repo", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    public ResponseEntity<?> createRepository(@RequestBody Map<String, String> body) {

        String nickname = body.get("lecturer_nickname");
        String repoName = body.get("repo_name");

        System.out.println(nickname);
        System.out.println(repoName);

        Lecturer lecturer = lecturerRepository.findByGitNick(nickname);
        if (lecturer == null) {
            return new ResponseEntity<>("Lecturer not found. Invalid git nickname.", HttpStatus.BAD_REQUEST);
        }
        String accessToken = lecturer.getGitToken();

        String uri ="/user/repos";
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", repoName);

        var headersSpec = prepareGitHubRequest(webClient.post(), uri, map)
                .header(HttpHeaders.AUTHORIZATION, "token " + accessToken); // specify additional requests

        Mono<String> r = getResponseFromGitHub(headersSpec);

        if (Objects.equals(r.block(), "Error response")) {
            return new ResponseEntity<>("Error creating repo on github", HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>("Repo created", HttpStatus.CREATED);
        }

    }

    public static Mono<String> getResponseFromGitHub(WebClient.RequestHeadersSpec<?> headersSpec){
        return headersSpec.exchangeToMono(response -> {
            if (response.statusCode().equals(HttpStatus.OK)) {
                return response.bodyToMono(String.class);
            } else if (response.statusCode().is4xxClientError()) {
                return Mono.just("Error response");
            } else {
                return response.createException()
                        .flatMap(Mono::error);
            }
        });

    }

    public static RequestHeadersSpec<?> prepareGitHubRequest(UriSpec<RequestBodySpec> uriSpec, String uri,
                                                             LinkedMultiValueMap<String, String> body){
        return uriSpec
                .uri(uri)
                .body(BodyInserters.fromMultipartData(body))
                .header(HttpHeaders.ACCEPT, "application/vnd.github.v3+json");

    }

    public static RequestHeadersSpec<?> prepareGitHubRequest(UriSpec<RequestBodySpec> uriSpec, String uri,
                                                             LinkedMultiValueMap<String, String> body, String username, String password){
        return
                uriSpec
                        .uri(uri)
                        .body(BodyInserters.fromMultipartData(body))
                        .headers(httpHeaders -> httpHeaders.setBasicAuth(username, password))
                        .header(HttpHeaders.ACCEPT, "application/vnd.github.v3+json");

    }

}
