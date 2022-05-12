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
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.UriSpec;
import reactor.core.publisher.Mono;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.Objects;


@RestController
@RequestMapping(value = "/git")
@RequiredArgsConstructor
public class RepositoriesController extends AbstractGitController {

    private final LecturerRepository lecturerRepository;

    @RequestMapping(value = "/repo", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    public ResponseEntity<?> createRepository(@RequestBody Map<String, String> body) {

        String nickname = body.get("lecturer_nickname");
        String repoName = body.get("repo_name");

        Lecturer lecturer = lecturerRepository.findByGitNick(nickname);
        if (lecturer == null) {
            return new ResponseEntity<>("Lecturer not found. Invalid git nickname.", HttpStatus.BAD_REQUEST);
        }

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

    public static Mono<String> getResponseFromGitHub(WebClient.RequestHeadersSpec<?> headersSpec){
        return headersSpec.exchangeToMono(response -> {
            if (response.statusCode().is2xxSuccessful()) {
                return response.bodyToMono(String.class);
            } else if (response.statusCode().is4xxClientError()) {
                System.out.println(response.statusCode());
                System.out.println(response.logPrefix());
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
                                                             String body, String username, String password){
        return
                uriSpec
                        .uri(uri)
                        .body(BodyInserters.fromValue(body))
                        .headers(httpHeaders -> httpHeaders.setBasicAuth(username, password))
                        .header(HttpHeaders.ACCEPT, "application/vnd.github.v3+json");

    }

    public static RequestHeadersSpec<?> prepareGitHubRequest(RequestHeadersUriSpec<?> requestHeadersUriSpec, String uri) {

        return requestHeadersUriSpec.uri(uri).header(HttpHeaders.ACCEPT, "application/vnd.github.v3+json");

    }

    public static RequestHeadersSpec<?> prepareGitHubRequest(RequestHeadersUriSpec<?> requestHeadersUriSpec, String uri,
                                                             Map<String, Object> queryParams, String username, String password) {
        return requestHeadersUriSpec.uri(uriBuilder -> {
                    uriBuilder
                            .path(uri);
                    queryParams.forEach(uriBuilder::queryParam);
                    return uriBuilder.build();
                }).header(HttpHeaders.ACCEPT, "application/vnd.github.v3+json")
                .headers(httpHeaders -> httpHeaders.setBasicAuth(username, password));
    }



}
