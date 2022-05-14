package com.iozeta.SpringIOZeta.Controllers.git;

import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import javax.ws.rs.core.HttpHeaders;
import java.util.Map;

public abstract class AbstractGitController {

    protected String baseUrl = "https://api.github.com";

    protected ClientHttpConnector connector() {
        return new
                ReactorClientHttpConnector(HttpClient.create(ConnectionProvider.newConnection()));
    }

    protected WebClient.RequestBodyUriSpec post(){
        return WebClient.builder().clientConnector(connector()).baseUrl(baseUrl).build().post();
    }

    protected WebClient.RequestHeadersUriSpec<?> get(){
        return WebClient.builder().clientConnector(connector()).baseUrl(baseUrl).build().get();
    }

    protected WebClient.RequestBodyUriSpec put(){
        return WebClient.builder().clientConnector(connector()).baseUrl(baseUrl).build().put();
    }

    public static WebClient.RequestHeadersSpec<?> prepareGitHubRequest(WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec, String uri,
                                                                       LinkedMultiValueMap<String, String> body){
        return uriSpec
                .uri(uri)
                .body(BodyInserters.fromMultipartData(body))
                .header(HttpHeaders.ACCEPT, "application/vnd.github.v3+json");

    }

    public static WebClient.RequestHeadersSpec<?> prepareGitHubRequest(WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec, String uri,
                                                                       String body, String username, String password){
        return
                uriSpec
                        .uri(uri)
                        .body(BodyInserters.fromValue(body))
                        .headers(httpHeaders -> httpHeaders.setBasicAuth(username, password))
                        .header(HttpHeaders.ACCEPT, "application/vnd.github.v3+json");

    }

    public static WebClient.RequestHeadersSpec<?> prepareGitHubRequest(WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec, String uri) {

        return requestHeadersUriSpec.uri(uri).header(HttpHeaders.ACCEPT, "application/vnd.github.v3+json");

    }

    public static WebClient.RequestHeadersSpec<?> prepareGitHubRequest(WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec, String uri,
                                                                       Map<String, Object> queryParams, String username, String password) {
        return requestHeadersUriSpec.uri(uriBuilder -> {
            uriBuilder
                    .path(uri);
            queryParams.forEach(uriBuilder::queryParam);
            return uriBuilder.build();
        }).header(HttpHeaders.ACCEPT, "application/vnd.github.v3+json")
                .headers(httpHeaders -> httpHeaders.setBasicAuth(username, password));
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


}

