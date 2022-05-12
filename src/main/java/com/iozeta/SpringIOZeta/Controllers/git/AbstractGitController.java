package com.iozeta.SpringIOZeta.Controllers.git;

import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

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


}

